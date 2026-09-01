package com.software.knowledgehub.ai.service;

import com.software.knowledgehub.ai.dto.AiChatDTO;
import com.software.knowledgehub.ai.vo.AiChatVO;
import reactor.core.publisher.Flux;

public interface AiChatService {

    /**
     * 同步生成 AI 回答。
     */
    AiChatVO chat(AiChatDTO request);

    /**
     * 流式生成 AI 回答片段。
     */
    Flux<String> streamChat(AiChatDTO request);
}
