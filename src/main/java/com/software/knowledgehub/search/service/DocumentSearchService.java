package com.software.knowledgehub.search.service;

import com.software.knowledgehub.search.dto.SearchDocumentQueryDTO;
import com.software.knowledgehub.search.vo.SearchDocumentVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentSearchService {

    /**
     * 重建已发布文档的搜索索引。
     */
    int rebuildDocumentIndex();

    /**
     * 分页搜索已发布文档。
     */
    Page<SearchDocumentVO> searchDocuments(
            SearchDocumentQueryDTO request,
            Pageable pageable);
}
