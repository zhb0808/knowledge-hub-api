package com.software.knowledgehub.knowledge.controller;

import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.knowledge.dto.BatchUpdateDocumentStatusDTO;
import com.software.knowledgehub.knowledge.dto.CreateDocumentDTO;
import com.software.knowledgehub.knowledge.dto.DocumentQueryDTO;
import com.software.knowledgehub.knowledge.dto.UpdateDocumentDTO;
import com.software.knowledgehub.knowledge.service.DocumentService;
import com.software.knowledgehub.knowledge.vo.DocumentListVO;
import com.software.knowledgehub.knowledge.vo.DocumentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ApiResponse<DocumentVO> createDocument(
            @Valid @RequestBody CreateDocumentDTO request) {
        return ApiResponse.success(documentService.createDocument(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<DocumentVO> getDocument(@PathVariable Long id) {
        return ApiResponse.success(documentService.getDocument(id));
    }

    @GetMapping
    public ApiResponse<Page<DocumentListVO>> listDocuments(
            @Valid DocumentQueryDTO request,
            @PageableDefault(
                    size = 10,
                    sort = "createdTime",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return ApiResponse.success(
                documentService.listDocuments(request, pageable));
    }

    @PatchMapping("/status")
    public ApiResponse<Integer> batchUpdateStatus(
            @Valid @RequestBody BatchUpdateDocumentStatusDTO request) {
        return ApiResponse.success(documentService.batchUpdateStatus(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDocumentDTO request) {
        documentService.updateDocument(id, request);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ApiResponse.success(null);
    }
}
