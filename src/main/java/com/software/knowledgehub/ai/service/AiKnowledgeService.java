package com.software.knowledgehub.ai.service;

import com.software.knowledgehub.ai.dto.AiChatDTO;
import com.software.knowledgehub.ai.vo.AiKnowledgeChatVO;
import com.software.knowledgehub.ai.vo.AiKnowledgeRebuildVO;

public interface AiKnowledgeService {

    /**
     * 根据企业知识生成同步回答。
     */
    AiKnowledgeChatVO knowledgeChat(AiChatDTO request);

    /**
     * 重建已发布文档的企业知识向量。
     */
    AiKnowledgeRebuildVO rebuildKnowledge();
}
