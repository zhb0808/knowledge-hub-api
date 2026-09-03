package com.software.knowledgehub.ai.service.impl;

import com.software.knowledgehub.ai.dto.AiConversationDTO;
import com.software.knowledgehub.ai.service.AiChatService;
import com.software.knowledgehub.ai.tool.KnowledgeDocumentTools;
import com.software.knowledgehub.ai.vo.AiChatVO;
import com.software.knowledgehub.ai.vo.AiTokenUsageVO;
import com.software.knowledgehub.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private static final String SYSTEM_PROMPT = """
            你是企业知识库平台中的通用 AI 助手。
            请使用准确、清晰、简洁的中文回答用户问题。
            用户询问系统中最近更新的已发布文档时，必须使用工具查询，不要猜测。
            如果无法确定答案，请明确说明不知道，不要编造事实。
            """;

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final MessageChatMemoryAdvisor messageChatMemoryAdvisor;
    private final KnowledgeDocumentTools knowledgeDocumentTools;

    /**
     * 同步生成 AI 回答。
     */
    @Override
    public AiChatVO chat(AiConversationDTO request, Long userId) {
        try {
            String memoryConversationId = buildMemoryConversationId(
                    userId,
                    request.getConversationId()
            );
            // 加载当前对话记忆，并允许大模型按需查询最近更新的已发布文档。
            ChatResponse response = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(request.getMessage())
                    .advisors(advisor -> advisor
                            .advisors(messageChatMemoryAdvisor)
                            .param(ChatMemory.CONVERSATION_ID, memoryConversationId))
                    .tools(knowledgeDocumentTools)
                    .call()
                    .chatResponse();
            return new AiChatVO(
                    response.getResult().getOutput().getText(),
                    toTokenUsage(response.getMetadata().getUsage())
            );
        } catch (Exception exception) {
            log.error("调用大模型生成同步回答失败", exception);
            throw new BusinessException("AI回答生成失败，请稍后重试");
        }
    }

    /**
     * 流式生成 AI 回答片段。
     */
    @Override
    public Flux<ServerSentEvent<Object>> streamChat(AiConversationDTO request, Long userId) {
        String memoryConversationId = buildMemoryConversationId(
                userId,
                request.getConversationId()
        );
        // 加载当前对话记忆，并保留流式响应中的回答片段和Token统计信息。
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(request.getMessage())
                .advisors(advisor -> advisor
                        .advisors(messageChatMemoryAdvisor)
                        .param(ChatMemory.CONVERSATION_ID, memoryConversationId))
                .tools(knowledgeDocumentTools)
                .stream()
                .chatResponse()
                .concatMap(response -> {
                    List<ServerSentEvent<Object>> events = new ArrayList<>();
                    // 将模型回答转换成 content 事件。
                    String content = response.getResult() == null ? null : response.getResult().getOutput().getText();
                    if (StringUtils.hasText(content)) {
                        events.add(ServerSentEvent.builder()
                                .event("content")
                                .data((Object) content)
                                .build());
                    }

                    // 流式响应结束时，将模型用量转换成 usage 事件。
                    Usage usage = response.getMetadata().getUsage();
                    if (usage != null && usage.getTotalTokens() != null && usage.getTotalTokens() > 0) {
                        events.add(ServerSentEvent.builder()
                                .event("usage")
                                .data((Object) toTokenUsage(usage))
                                .build());
                    }
                    return Flux.fromIterable(events);
                })
                .doOnError(exception -> log.error("调用大模型生成流式回答失败", exception));
    }

    /**
     * 清除当前用户的一段对话记忆。
     */
    @Override
    public void clearConversation(String conversationId, Long userId) {
        chatMemory.clear(buildMemoryConversationId(userId, conversationId));
    }

    private String buildMemoryConversationId(Long userId, String conversationId) {
        return userId + ":" + conversationId;
    }

    private AiTokenUsageVO toTokenUsage(Usage usage) {
        return new AiTokenUsageVO(
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens()
        );
    }
}
