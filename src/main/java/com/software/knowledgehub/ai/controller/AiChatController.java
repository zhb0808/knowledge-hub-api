package com.software.knowledgehub.ai.controller;

import com.software.knowledgehub.ai.dto.AiChatDTO;
import com.software.knowledgehub.ai.service.AiChatService;
import com.software.knowledgehub.ai.vo.AiChatVO;
import com.software.knowledgehub.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    public ApiResponse<AiChatVO> chat(@Valid @RequestBody AiChatDTO request) {
        return ApiResponse.success(aiChatService.chat(request));
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@Valid @RequestBody AiChatDTO request) {
        return aiChatService.streamChat(request);
    }
}
