package com.software.knowledgehub.knowledge.service.impl;

import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.knowledge.dto.CreateKnowledgeBaseDTO;
import com.software.knowledgehub.knowledge.dto.UpdateKnowledgeBaseDTO;
import com.software.knowledgehub.knowledge.entity.KbKnowledgeBase;
import com.software.knowledgehub.knowledge.repository.KbCategoryRepository;
import com.software.knowledgehub.knowledge.repository.KbDocumentRepository;
import com.software.knowledgehub.knowledge.repository.KbKnowledgeBaseRepository;
import com.software.knowledgehub.knowledge.repository.KbTagRepository;
import com.software.knowledgehub.knowledge.service.KnowledgeBaseService;
import com.software.knowledgehub.knowledge.vo.KnowledgeBaseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KnowledgeBaseServiceImpl implements KnowledgeBaseService {

    private final KbKnowledgeBaseRepository knowledgeBaseRepository;
    private final KbCategoryRepository categoryRepository;
    private final KbDocumentRepository documentRepository;
    private final KbTagRepository tagRepository;

    /**
     * 创建知识库。
     */
    @Override
    @Transactional
    public KnowledgeBaseVO createKnowledgeBase(CreateKnowledgeBaseDTO request) {
        String code = request.getCode().strip().toUpperCase(Locale.ROOT);

        // 检查知识库编码是否已经使用。
        if (knowledgeBaseRepository.existsByCode(code)) {
            throw new BusinessException("知识库编码已存在");
        }

        KbKnowledgeBase knowledgeBase = new KbKnowledgeBase();
        knowledgeBase.setCode(code);
        knowledgeBase.setName(request.getName().strip());
        knowledgeBase.setDescription(request.getDescription());
        knowledgeBase.setStatus((short) 1);
        // 保存知识库并取得数据库生成的主键。
        KbKnowledgeBase savedKnowledgeBase = knowledgeBaseRepository.save(knowledgeBase);
        return toKnowledgeBaseVO(savedKnowledgeBase);
    }

    /**
     * 查询知识库详情。
     */
    @Override
    public KnowledgeBaseVO getKnowledgeBase(Long id) {
        // 在只读事务中加载知识库。
        KbKnowledgeBase knowledgeBase = knowledgeBaseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("知识库不存在"));
        return toKnowledgeBaseVO(knowledgeBase);
    }

    /**
     * 分页查询知识库。
     */
    @Override
    public Page<KnowledgeBaseVO> listKnowledgeBases(Pageable pageable) {
        // 按创建时间倒序加载知识库分页数据。
        return knowledgeBaseRepository.findAll(pageable).map(this::toKnowledgeBaseVO);
    }

    /**
     * 修改知识库资料和状态。
     */
    @Override
    @Transactional
    public void updateKnowledgeBase(Long id, UpdateKnowledgeBaseDTO request) {
        // 加载需要修改的托管实体。
        KbKnowledgeBase knowledgeBase = knowledgeBaseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("知识库不存在"));

        knowledgeBase.setName(request.getName().strip());
        knowledgeBase.setDescription(request.getDescription());
        knowledgeBase.setStatus(request.getStatus());
    }

    /**
     * 删除不包含业务数据的知识库。
     */
    @Override
    @Transactional
    public void deleteKnowledgeBase(Long id) {
        // 加载知识库，避免把不存在的删除当作成功。
        KbKnowledgeBase knowledgeBase = knowledgeBaseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("知识库不存在"));

        // 检查分类、文档和标签对知识库的业务引用。
        if (categoryRepository.existsByKnowledgeBaseId(id)
                || documentRepository.existsByKnowledgeBaseId(id)
                || tagRepository.existsByKnowledgeBaseId(id)) {
            throw new BusinessException("知识库下仍有分类、文档或标签，不能删除");
        }

        knowledgeBaseRepository.delete(knowledgeBase);
    }

    private KnowledgeBaseVO toKnowledgeBaseVO(KbKnowledgeBase knowledgeBase) {
        return new KnowledgeBaseVO(
                knowledgeBase.getId(),
                knowledgeBase.getCode(),
                knowledgeBase.getName(),
                knowledgeBase.getDescription(),
                knowledgeBase.getStatus(),
                knowledgeBase.getCreatedTime(),
                knowledgeBase.getUpdatedTime()
        );
    }

}
