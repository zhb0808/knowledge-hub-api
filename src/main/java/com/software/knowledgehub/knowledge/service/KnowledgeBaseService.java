package com.software.knowledgehub.knowledge.service;

import com.software.knowledgehub.knowledge.dto.CreateKnowledgeBaseDTO;
import com.software.knowledgehub.knowledge.dto.UpdateKnowledgeBaseDTO;
import com.software.knowledgehub.knowledge.vo.KnowledgeBaseVO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface KnowledgeBaseService {

    /**
     * 创建知识库。
     */
    KnowledgeBaseVO createKnowledgeBase(CreateKnowledgeBaseDTO request);

    /**
     * 查询知识库详情。
     */
    KnowledgeBaseVO getKnowledgeBase(Long id);

    /**
     * 分页查询知识库。
     */
    Page<KnowledgeBaseVO> listKnowledgeBases(Pageable pageable);

    /**
     * 修改知识库资料和状态。
     */
    void updateKnowledgeBase(Long id, UpdateKnowledgeBaseDTO request);

    /**
     * 删除不包含业务数据的知识库。
     */
    void deleteKnowledgeBase(Long id);
}
