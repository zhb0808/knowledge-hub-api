package com.software.knowledgehub.ai.service;

import com.software.knowledgehub.ai.dto.AiChatDTO;
import com.software.knowledgehub.ai.vo.AiKnowledgeChatVO;
import com.software.knowledgehub.ai.vo.AiKnowledgeRebuildVO;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface AiKnowledgeService {

    /**
     * 根据企业知识生成同步回答。
     */
    AiKnowledgeChatVO knowledgeChat(AiChatDTO request);

    /**
     * 根据企业知识流式生成回答。
     */
    Flux<ServerSentEvent<Object>> streamKnowledgeChat(AiChatDTO request);

    /**
     * 重建已发布文档的企业知识向量。
     */
    AiKnowledgeRebuildVO rebuildKnowledge();
}
