package com.software.knowledgehub.knowledge.controller;

import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.knowledge.service.DocumentFileService;
import com.software.knowledgehub.knowledge.service.DocumentService;
import com.software.knowledgehub.knowledge.vo.DocumentFileAccessVO;
import com.software.knowledgehub.knowledge.vo.DocumentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/published-documents")
@RequiredArgsConstructor
public class PublishedDocumentController {

    private final DocumentService documentService;
    private final DocumentFileService documentFileService;

    @GetMapping("/{id}")
    public ApiResponse<DocumentVO> getPublishedDocument(@PathVariable Long id) {
        return ApiResponse.success(documentService.getPublishedDocument(id));
    }

    @GetMapping("/{id}/file-url")
    public ApiResponse<DocumentFileAccessVO> getPublishedFileUrl(@PathVariable Long id) {
        return ApiResponse.success(documentFileService.getPublishedFileUrl(id));
    }
}
