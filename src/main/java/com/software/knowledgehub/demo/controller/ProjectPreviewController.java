package com.software.knowledgehub.demo.controller;

import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.demo.dto.KnowledgeBasePreviewDTO;
import com.software.knowledgehub.demo.service.ProjectPreviewService;
import com.software.knowledgehub.demo.vo.KnowledgeBasePreviewVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo/knowledge-bases")
@RequiredArgsConstructor
public class ProjectPreviewController {

    private final ProjectPreviewService projectPreviewService;

    @PostMapping("/preview")
    public ApiResponse<KnowledgeBasePreviewVO> createPreview(
            @Valid @RequestBody KnowledgeBasePreviewDTO request) {
        return ApiResponse.success(projectPreviewService.createKnowledgeBasePreview(request));
    }
}
