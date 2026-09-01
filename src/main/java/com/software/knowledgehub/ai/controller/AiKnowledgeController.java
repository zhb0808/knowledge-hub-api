package com.software.knowledgehub.ai.controller;

import com.software.knowledgehub.ai.dto.AiChatDTO;
import com.software.knowledgehub.ai.service.AiKnowledgeService;
import com.software.knowledgehub.ai.vo.AiKnowledgeChatVO;
import com.software.knowledgehub.ai.vo.AiKnowledgeRebuildVO;
import com.software.knowledgehub.audit.annotation.OperationLog;
import com.software.knowledgehub.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiKnowledgeController {

    private final AiKnowledgeService aiKnowledgeService;

    @PostMapping("/knowledge-chat")
    public ApiResponse<AiKnowledgeChatVO> knowledgeChat(
            @Valid @RequestBody AiChatDTO request) {
        return ApiResponse.success(aiKnowledgeService.knowledgeChat(request));
    }

    @OperationLog(module = "AI知识问答", action = "重建企业知识向量")
    @PostMapping("/knowledge/rebuild")
    public ApiResponse<AiKnowledgeRebuildVO> rebuildKnowledge() {
        return ApiResponse.success(aiKnowledgeService.rebuildKnowledge());
    }
}
