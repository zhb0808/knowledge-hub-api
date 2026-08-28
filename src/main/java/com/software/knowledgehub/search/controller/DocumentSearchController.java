package com.software.knowledgehub.search.controller;

import com.software.knowledgehub.audit.annotation.OperationLog;
import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.search.dto.SearchDocumentQueryDTO;
import com.software.knowledgehub.search.service.DocumentSearchService;
import com.software.knowledgehub.search.vo.SearchDocumentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search/documents")
@RequiredArgsConstructor
public class DocumentSearchController {

    private final DocumentSearchService documentSearchService;

    @GetMapping
    public ApiResponse<Page<SearchDocumentVO>> searchDocuments(
            @Valid SearchDocumentQueryDTO request,
            Pageable pageable) {
        return ApiResponse.success(documentSearchService.searchDocuments(request, pageable));
    }

    @OperationLog(module = "企业搜索", action = "重建文档搜索索引")
    @PostMapping("/rebuild")
    public ApiResponse<Integer> rebuildDocumentIndex() {
        return ApiResponse.success(documentSearchService.rebuildDocumentIndex());
    }
}
