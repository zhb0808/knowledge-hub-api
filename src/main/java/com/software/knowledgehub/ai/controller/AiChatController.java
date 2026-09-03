package com.software.knowledgehub.ai.controller;

import com.software.knowledgehub.ai.dto.AiConversationDTO;
import com.software.knowledgehub.ai.service.AiChatService;
import com.software.knowledgehub.ai.vo.AiChatVO;
import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.security.model.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/ai/chat")
@RequiredArgsConstructor
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping
    public ApiResponse<AiChatVO> chat(
            @Valid @RequestBody AiConversationDTO request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.success(aiChatService.chat(request, user.getId()));
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Object>> streamChat(
            @Valid @RequestBody AiConversationDTO request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return aiChatService.streamChat(request, user.getId());
    }

    @DeleteMapping("/conversations/{conversationId}")
    public ApiResponse<Void> clearConversation(
            @PathVariable String conversationId,
            @AuthenticationPrincipal AuthenticatedUser user) {
        aiChatService.clearConversation(conversationId, user.getId());
        return ApiResponse.success(null);
    }
}
