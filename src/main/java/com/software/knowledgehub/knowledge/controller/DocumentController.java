package com.software.knowledgehub.knowledge.controller;

import com.software.knowledgehub.audit.annotation.OperationLog;
import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.knowledge.dto.BatchUpdateDocumentStatusDTO;
import com.software.knowledgehub.knowledge.dto.CreateDocumentDTO;
import com.software.knowledgehub.knowledge.dto.DocumentQueryDTO;
import com.software.knowledgehub.knowledge.dto.UpdateDocumentDTO;
import com.software.knowledgehub.knowledge.service.DocumentService;
import com.software.knowledgehub.knowledge.service.DocumentFileService;
import com.software.knowledgehub.knowledge.vo.DocumentFileAccessVO;
import com.software.knowledgehub.knowledge.vo.DocumentFileVO;
import com.software.knowledgehub.knowledge.vo.DocumentListVO;
import com.software.knowledgehub.knowledge.vo.DocumentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
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
    private final DocumentFileService documentFileService;

    @OperationLog(module = "文档管理", action = "创建文档")
    @PostMapping
    public ApiResponse<DocumentVO> createDocument(
            @Valid @RequestBody CreateDocumentDTO request) {
        return ApiResponse.success(documentService.createDocument(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<DocumentVO> getDocument(@PathVariable Long id) {
        return ApiResponse.success(documentService.getDocument(id));
    }

    @GetMapping("/{id}/file")
    public ApiResponse<DocumentFileVO> getFile(@PathVariable Long id) {
        return ApiResponse.success(documentFileService.getFile(id));
    }

    @GetMapping
    public ApiResponse<Page<DocumentListVO>> listDocuments(
            @Valid DocumentQueryDTO request,
            @SortDefault(
                    sort = "createdTime",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return ApiResponse.success(
                documentService.listDocuments(request, pageable));
    }

    @OperationLog(module = "文档管理", action = "批量修改文档状态")
    @PatchMapping("/status")
    public ApiResponse<Integer> batchUpdateStatus(
            @Valid @RequestBody BatchUpdateDocumentStatusDTO request) {
        return ApiResponse.success(documentService.batchUpdateStatus(request));
    }

    @OperationLog(module = "文档管理", action = "修改文档")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDocumentDTO request) {
        documentService.updateDocument(id, request);
        return ApiResponse.success(null);
    }

    @OperationLog(module = "文档管理", action = "删除文档")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ApiResponse.success(null);
    }

    @OperationLog(module = "文档管理", action = "上传文档文件")
    @PostMapping("/{id}/file")
    public ApiResponse<DocumentFileVO> uploadFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(documentFileService.uploadFile(id, file));
    }

    @OperationLog(module = "文档管理", action = "替换文档文件")
    @PutMapping("/{id}/file")
    public ApiResponse<DocumentFileVO> replaceFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ApiResponse.success(documentFileService.replaceFile(id, file));
    }

    @OperationLog(module = "文档管理", action = "删除文档文件")
    @DeleteMapping("/{id}/file")
    public ApiResponse<Void> deleteFile(@PathVariable Long id) {
        documentFileService.deleteFile(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}/file-url")
    public ApiResponse<DocumentFileAccessVO> getFileUrl(@PathVariable Long id) {
        return ApiResponse.success(documentFileService.getFileUrl(id));
    }
}
