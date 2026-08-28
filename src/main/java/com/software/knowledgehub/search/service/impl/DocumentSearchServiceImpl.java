package com.software.knowledgehub.search.service.impl;

import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.knowledge.entity.KbDocument;
import com.software.knowledgehub.knowledge.repository.KbDocumentRepository;
import com.software.knowledgehub.search.dto.SearchDocumentQueryDTO;
import com.software.knowledgehub.search.entity.SearchDocument;
import com.software.knowledgehub.search.repository.SearchDocumentRepository;
import com.software.knowledgehub.search.service.DocumentSearchService;
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
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentSearchServiceImpl implements DocumentSearchService {

    private final KbDocumentRepository documentRepository;
    private final SearchDocumentRepository searchDocumentRepository;
    private final ElasticsearchOperations elasticsearchOperations;

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
            NativeQuery query = NativeQuery.builder()
                    .withQuery(queryBuilder -> queryBuilder.multiMatch(multiMatch -> multiMatch
                            .query(request.getKeyword())
                            .fields("title", "summary", "content")))
                    .withPageable(pageable)
                    .build();

            SearchHits<SearchDocument> searchHits = elasticsearchOperations.search(
                    query,
                    SearchDocument.class
            );
            List<SearchDocumentVO> records = searchHits.getSearchHits().stream()
                    .map(searchHit -> {
                        SearchDocument document = searchHit.getContent();
                        return new SearchDocumentVO(
                                document.getId(),
                                document.getKnowledgeBaseId(),
                                document.getKnowledgeBaseName(),
                                document.getCategoryId(),
                                document.getCategoryName(),
                                document.getTitle(),
                                document.getSummary(),
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
