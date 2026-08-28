package com.software.knowledgehub.search.repository;

import com.software.knowledgehub.search.entity.SearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchDocumentRepository extends ElasticsearchRepository<SearchDocument, Long> {
}
