package com.software.knowledgehub.knowledge.controller;

import com.software.knowledgehub.audit.annotation.OperationLog;
import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.knowledge.dto.CreateKnowledgeBaseDTO;
import com.software.knowledgehub.knowledge.dto.UpdateKnowledgeBaseDTO;
import com.software.knowledgehub.knowledge.service.KnowledgeBaseService;
import com.software.knowledgehub.knowledge.vo.KnowledgeBaseVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/knowledge-bases")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    @OperationLog(module = "知识库管理", action = "创建知识库")
    @PostMapping
    public ApiResponse<KnowledgeBaseVO> createKnowledgeBase(
            @Valid @RequestBody CreateKnowledgeBaseDTO request) {
        return ApiResponse.success(knowledgeBaseService.createKnowledgeBase(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<KnowledgeBaseVO> getKnowledgeBase(@PathVariable Long id) {
        return ApiResponse.success(knowledgeBaseService.getKnowledgeBase(id));
    }

    @GetMapping
    public ApiResponse<Page<KnowledgeBaseVO>> listKnowledgeBases(
            @SortDefault(
                    sort = "createdTime",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return ApiResponse.success(knowledgeBaseService.listKnowledgeBases(pageable));
    }

    @OperationLog(module = "知识库管理", action = "修改知识库")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateKnowledgeBase(
            @PathVariable Long id,
            @Valid @RequestBody UpdateKnowledgeBaseDTO request) {
        knowledgeBaseService.updateKnowledgeBase(id, request);
        return ApiResponse.success(null);
    }

    @OperationLog(module = "知识库管理", action = "删除知识库")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteKnowledgeBase(@PathVariable Long id) {
        knowledgeBaseService.deleteKnowledgeBase(id);
        return ApiResponse.success(null);
    }
}
