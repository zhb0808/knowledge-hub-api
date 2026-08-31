package com.software.knowledgehub.search.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.software.knowledgehub.common.config.RabbitMqConfig;
import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.knowledge.entity.KbDocument;
import com.software.knowledgehub.knowledge.repository.KbDocumentRepository;
import com.software.knowledgehub.search.dto.SearchDocumentQueryDTO;
import com.software.knowledgehub.search.entity.SearchDocument;
import com.software.knowledgehub.search.message.DocumentIndexRebuildMessage;
import com.software.knowledgehub.search.repository.SearchDocumentRepository;
import com.software.knowledgehub.search.service.DocumentSearchService;
import com.software.knowledgehub.search.vo.DocumentIndexRebuildTaskVO;
import com.software.knowledgehub.search.vo.SearchDocumentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentSearchServiceImpl implements DocumentSearchService {

    private final KbDocumentRepository documentRepository;
    private final SearchDocumentRepository searchDocumentRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 提交文档搜索索引重建任务。
     */
    @Override
    public DocumentIndexRebuildTaskVO submitDocumentIndexRebuild() {
        String taskId = UUID.randomUUID().toString();
        DocumentIndexRebuildMessage message = new DocumentIndexRebuildMessage(
                taskId,
                OffsetDateTime.now()
        );

        try {
            // 将重建任务持久化投递到RabbitMQ，由后台消费者执行耗时操作。
            rabbitTemplate.convertAndSend(
                    RabbitMqConfig.DOCUMENT_INDEX_EXCHANGE,
                    RabbitMqConfig.DOCUMENT_INDEX_REBUILD_ROUTING_KEY,
                    message,
                    rabbitMessage -> {
                        rabbitMessage.getMessageProperties()
                                .setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        return rabbitMessage;
                    }
            );
            return new DocumentIndexRebuildTaskVO(taskId, "SUBMITTED");
        } catch (AmqpException exception) {
            log.error("提交文档索引重建任务失败，taskId={}", taskId, exception);
            throw new BusinessException("提交索引重建任务失败，请检查RabbitMQ服务是否正常");
        }
    }

    /**
     * 重建已发布文档的搜索索引。
     */
    @Override
    public int rebuildDocumentIndex() {
        try {
            // 从主数据库读取当前全部已发布文档。
            List<KbDocument> documents = documentRepository.findByStatus("PUBLISHED");

            // 删除旧索引，避免归档或删除的文档继续出现在搜索结果中。
            IndexOperations indexOperations = elasticsearchOperations.indexOps(SearchDocument.class);
            if (indexOperations.exists() && !indexOperations.delete()) {
                throw new BusinessException("删除旧搜索索引失败");
            }
            if (!indexOperations.createWithMapping()) {
                throw new BusinessException("创建搜索索引失败");
            }

            List<SearchDocument> searchDocuments = documents.stream()
                    .map(document -> new SearchDocument(
                            document.getId(),
                            document.getTitle(),
                            document.getSummary(),
                            document.getContent(),
                            document.getKnowledgeBase().getId(),
                            document.getKnowledgeBase().getName(),
                            document.getCategory() == null ? null : document.getCategory().getId(),
                            document.getCategory() == null ? null : document.getCategory().getName(),
                            document.getTags().stream().map(tag -> tag.getId()).toList(),
                            document.getStatus(),
                            document.getUpdatedTime()
                    ))
                    .toList();

            // 将数据库文档批量写入搜索索引。
            searchDocumentRepository.saveAll(searchDocuments);
            // 刷新索引，保证重建成功返回后可以立即搜索到新数据。
            indexOperations.refresh();
            return searchDocuments.size();
        } catch (BusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error("重建文档搜索索引失败", exception);
            throw new BusinessException("重建搜索索引失败，请检查Elasticsearch服务是否正常");
        }
    }

    /**
     * 分页搜索已发布文档。
     */
    @Override
    public Page<SearchDocumentVO> searchDocuments(
            SearchDocumentQueryDTO request,
            Pageable pageable) {
        try {
            List<Query> filters = new ArrayList<>();
            if (request.getKnowledgeBaseId() != null) {
                // 使用知识库ID缩小关键词搜索范围。
                filters.add(Query.of(query -> query.term(term -> term
                        .field("knowledgeBaseId")
                        .value(request.getKnowledgeBaseId()))));
            }
            if (request.getCategoryId() != null) {
                // 分类ID是精确业务条件，不参与相关度计算。
                filters.add(Query.of(query -> query.term(term -> term
                        .field("categoryId")
                        .value(request.getCategoryId()))));
            }
            if (request.getTagId() != null) {
                // 数组字段命中任一相同标签ID即可满足单标签筛选条件。
                filters.add(Query.of(query -> query.term(term -> term
                        .field("tagIds")
                        .value(request.getTagId()))));
            }

            NativeQuery query = NativeQuery.builder()
                    .withQuery(queryBuilder -> queryBuilder.bool(bool -> bool
                            // 关键词匹配决定文档是否命中，并参与相关度计算。
                            .must(must -> must.multiMatch(multiMatch -> multiMatch
                                    .query(request.getKeyword())
                                    .fields("title", "summary", "content")))
                            .filter(filters)))
                    .withHighlightQuery(new HighlightQuery(
                            new Highlight(
                                    HighlightParameters.builder()
                                            .withPreTags("<em>")
                                            .withPostTags("</em>")
                                            .withFragmentSize(100)
                                            .withNumberOfFragments(1)
                                            .build(),
                                    List.of(
                                            new HighlightField("title"),
                                            new HighlightField("summary"),
                                            new HighlightField("content")
                                    )
                            ),
                            SearchDocument.class
                    ))
                    .withPageable(pageable)
                    .build();

            SearchHits<SearchDocument> searchHits = elasticsearchOperations.search(
                    query,
                    SearchDocument.class
            );
            List<SearchDocumentVO> records = searchHits.getSearchHits().stream()
                    .map(searchHit -> {
                        SearchDocument document = searchHit.getContent();
                        List<String> titleHighlights = searchHit.getHighlightField("title");
                        List<String> summaryHighlights = searchHit.getHighlightField("summary");
                        List<String> contentHighlights = searchHit.getHighlightField("content");
                        return new SearchDocumentVO(
                                document.getId(),
                                document.getKnowledgeBaseId(),
                                document.getKnowledgeBaseName(),
                                document.getCategoryId(),
                                document.getCategoryName(),
                                titleHighlights.isEmpty() ? document.getTitle() : titleHighlights.get(0),
                                summaryHighlights.isEmpty() ? document.getSummary() : summaryHighlights.get(0),
                                contentHighlights.isEmpty() ? null : contentHighlights.get(0),
                                document.getUpdatedTime()
                        );
                    })
                    .toList();
            return new PageImpl<>(records, pageable, searchHits.getTotalHits());
        } catch (Exception exception) {
            log.error("搜索文档失败", exception);
            throw new BusinessException("搜索文档失败，请检查Elasticsearch服务是否正常");
        }
    }
}
