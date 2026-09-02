package com.software.knowledgehub.ai.service.impl;

import com.software.knowledgehub.ai.dto.AiChatDTO;
import com.software.knowledgehub.ai.model.AiKnowledgeContext;
import com.software.knowledgehub.ai.service.AiKnowledgeService;
import com.software.knowledgehub.ai.vo.AiKnowledgeChatVO;
import com.software.knowledgehub.ai.vo.AiKnowledgeRebuildVO;
import com.software.knowledgehub.ai.vo.AiKnowledgeSourceVO;
import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.knowledge.entity.KbDocument;
import com.software.knowledgehub.knowledge.repository.KbDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiKnowledgeServiceImpl implements AiKnowledgeService {

    private static final int RETRIEVAL_TOP_K = 3;
    private static final int EMBEDDING_BATCH_SIZE = 20;

    private static final String SYSTEM_PROMPT = """
            你是企业知识库平台中的知识问答助手。
            只能根据本次提供的企业参考资料回答问题，不要使用常见做法猜测企业规定。
            如果参考资料中没有足够依据，请明确说明当前企业知识库中没有找到相关规定。
            请使用准确、清晰、简洁的中文回答。
            """;

    private final KbDocumentRepository documentRepository;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;
    private final ChatClient chatClient;

    /**
     * 根据企业知识生成同步回答。
     */
    @Override
    public AiKnowledgeChatVO knowledgeChat(AiChatDTO request) {
        try {
            AiKnowledgeContext knowledgeContext = retrieveKnowledge(request.getMessage());
            if (knowledgeContext.getSources().isEmpty()) {
                return new AiKnowledgeChatVO(
                        "当前企业知识库中没有找到相关规定。",
                        List.of()
                );
            }

            // 把检索结果和用户问题一起提交给大模型生成回答。
            String answer = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(knowledgeContext.getUserPrompt())
                    .call()
                    .content();
            return new AiKnowledgeChatVO(answer, knowledgeContext.getSources());
        } catch (Exception exception) {
            log.error("生成企业知识回答失败", exception);
            throw new BusinessException("企业知识回答生成失败，请稍后重试");
        }
    }

    /**
     * 根据企业知识流式生成回答。
     */
    @Override
    public Flux<ServerSentEvent<Object>> streamKnowledgeChat(AiChatDTO request) {
        return Flux.defer(() -> {
                    AiKnowledgeContext knowledgeContext = retrieveKnowledge(request.getMessage());
                    ServerSentEvent<Object> sourcesEvent = ServerSentEvent.builder()
                            .event("sources")
                            .data(knowledgeContext.getSources())
                            .build();

                    if (knowledgeContext.getSources().isEmpty()) {
                        ServerSentEvent<Object> contentEvent = ServerSentEvent.builder()
                                .event("content")
                                .data("当前企业知识库中没有找到相关规定。")
                                .build();
                        return Flux.just(sourcesEvent, contentEvent);
                    }

                    // 先返回参考来源，再把模型生成的回答片段持续发送给客户端。
                    Flux<ServerSentEvent<Object>> contentEvents = chatClient.prompt()
                            .system(SYSTEM_PROMPT)
                            .user(knowledgeContext.getUserPrompt())
                            .stream()
                            .content()
                            .map(content -> ServerSentEvent.builder()
                                    .event("content")
                                    .data((Object) content)
                                    .build());
                    return Flux.concat(Flux.just(sourcesEvent), contentEvents);
                })
                .doOnError(exception -> log.error("生成企业知识流式回答失败", exception));
    }

    /**
     * 重建已发布文档的企业知识向量。
     */
    @Override
    @Transactional
    public AiKnowledgeRebuildVO rebuildKnowledge() {
        try {
            // 从主数据库读取全部已发布文档，只处理具有正文的内容。
            List<KbDocument> publishedDocuments = documentRepository.findByStatus("PUBLISHED");
            TokenTextSplitter textSplitter = TokenTextSplitter.builder()
                    .withChunkSize(300)
                    .withMinChunkSizeChars(100)
                    .withMinChunkLengthToEmbed(10)
                    .withMaxNumChunks(100)
                    .withKeepSeparator(true)
                    .withPunctuationMarks(List.of(
                            '.', '?', '!', ';', '\n',
                            '。', '？', '！', '；'
                    ))
                    .build();

            int documentCount = 0;
            List<Document> knowledgeChunks = new ArrayList<>();
            for (KbDocument document : publishedDocuments) {
                if (!StringUtils.hasText(document.getContent())) {
                    continue;
                }

                Map<String, Object> metadata = new HashMap<>();
                metadata.put("documentId", document.getId().toString());
                metadata.put("title", document.getTitle());
                metadata.put("knowledgeBaseId", document.getKnowledgeBase().getId().toString());

                List<Document> splitDocuments = textSplitter.apply(List.of(
                        new Document(document.getContent(), metadata)
                ));
                for (int index = 0; index < splitDocuments.size(); index++) {
                    Document splitDocument = splitDocuments.get(index);
                    Map<String, Object> chunkMetadata = new HashMap<>(metadata);
                    chunkMetadata.put("chunkIndex", index + 1);
                    knowledgeChunks.add(new Document(
                            "文档标题：" + document.getTitle()
                                    + "\n正文片段：" + splitDocument.getText(),
                            chunkMetadata
                    ));
                }
                documentCount++;
            }

            // 在同一事务中清理旧片段并写入新向量，失败时回滚本次重建。
            jdbcTemplate.update("DELETE FROM kb_document_chunk");
            for (int start = 0; start < knowledgeChunks.size(); start += EMBEDDING_BATCH_SIZE) {
                int end = Math.min(start + EMBEDDING_BATCH_SIZE, knowledgeChunks.size());
                // 向量模型单次最多处理20段文本，分批写入避免超过接口限制。
                vectorStore.add(knowledgeChunks.subList(start, end));
            }
            return new AiKnowledgeRebuildVO(documentCount, knowledgeChunks.size());
        } catch (Exception exception) {
            log.error("重建企业知识向量失败", exception);
            throw new BusinessException("重建企业知识失败，请检查 Embedding 模型和 pgvector 服务是否正常");
        }
    }

    private AiKnowledgeContext retrieveKnowledge(String message) {
        // 将用户问题转换为向量，并从企业知识片段中查找最相关的三条资料。
        List<Document> relevantDocuments = vectorStore.similaritySearch(SearchRequest.builder()
                .query(message)
                .topK(RETRIEVAL_TOP_K)
                .build());
        if (relevantDocuments == null || relevantDocuments.isEmpty()) {
            return new AiKnowledgeContext("", List.of());
        }

        StringBuilder context = new StringBuilder();
        List<AiKnowledgeSourceVO> sources = new ArrayList<>();
        for (int index = 0; index < relevantDocuments.size(); index++) {
            Document document = relevantDocuments.get(index);
            context.append("参考资料")
                    .append(index + 1)
                    .append("：\n")
                    .append(document.getText())
                    .append("\n\n");

            Map<String, Object> metadata = document.getMetadata();
            sources.add(new AiKnowledgeSourceVO(
                    Long.valueOf(metadata.get("documentId").toString()),
                    metadata.get("title").toString(),
                    document.getText()
            ));
        }

        String userPrompt = """
                以下是系统检索到的企业参考资料：

                %s
                用户问题：
                %s
                """.formatted(context, message);
        return new AiKnowledgeContext(userPrompt, sources);
    }
}
