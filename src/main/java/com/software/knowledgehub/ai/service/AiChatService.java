package com.software.knowledgehub.ai.service;

import com.software.knowledgehub.ai.dto.AiConversationDTO;
import com.software.knowledgehub.ai.vo.AiChatVO;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface AiChatService {

    /**
     * 同步生成 AI 回答。
     */
    AiChatVO chat(AiConversationDTO request, Long userId);

    /**
     * 流式生成 AI 回答片段。
     */
    Flux<ServerSentEvent<Object>> streamChat(AiConversationDTO request, Long userId);

    /**
     * 清除当前用户的一段对话记忆。
     */
    void clearConversation(String conversationId, Long userId);
}
