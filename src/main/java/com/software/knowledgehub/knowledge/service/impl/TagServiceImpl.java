package com.software.knowledgehub.knowledge.service.impl;

import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.knowledge.dto.CreateTagDTO;
import com.software.knowledgehub.knowledge.dto.UpdateTagDTO;
import com.software.knowledgehub.knowledge.entity.KbKnowledgeBase;
import com.software.knowledgehub.knowledge.entity.KbTag;
import com.software.knowledgehub.knowledge.repository.KbDocumentRepository;
import com.software.knowledgehub.knowledge.repository.KbKnowledgeBaseRepository;
import com.software.knowledgehub.knowledge.repository.KbTagRepository;
import com.software.knowledgehub.knowledge.service.TagService;
import com.software.knowledgehub.knowledge.vo.TagVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

    private final KbTagRepository tagRepository;
    private final KbKnowledgeBaseRepository knowledgeBaseRepository;
    private final KbDocumentRepository documentRepository;

    /**
     * 创建标签。
     */
    @Override
    @Transactional
    public TagVO createTag(CreateTagDTO request) {
        String name = request.getName().strip();

        // 加载标签所属知识库。
        KbKnowledgeBase knowledgeBase = knowledgeBaseRepository
                .findById(request.getKnowledgeBaseId())
                .orElseThrow(() -> new BusinessException("知识库不存在"));

        // 检查知识库内的标签名称。
        if (tagRepository.existsByKnowledgeBaseIdAndName(
                knowledgeBase.getId(), name)) {
            throw new BusinessException("知识库内已存在同名标签");
        }

        KbTag tag = new KbTag();
        tag.setKnowledgeBase(knowledgeBase);
        tag.setName(name);

        // 保存标签。
        return toTagVO(tagRepository.save(tag));
    }

    /**
     * 查询标签详情。
     */
    @Override
    public TagVO getTag(Long id) {
        // 按主键加载标签。
        KbTag tag = tagRepository.findById(id)
                .orElseThrow(() -> new BusinessException("标签不存在"));
        return toTagVO(tag);
    }

    /**
     * 查询知识库的标签列表。
     */
    @Override
    public List<TagVO> listTags(Long knowledgeBaseId) {
        // 先确认知识库存在，区分空列表与无效知识库。
        if (!knowledgeBaseRepository.existsById(knowledgeBaseId)) {
            throw new BusinessException("知识库不存在");
        }

        // 按标签名称加载列表。
        return tagRepository.findByKnowledgeBaseIdOrderByNameAsc(knowledgeBaseId)
                .stream()
                .map(this::toTagVO)
                .toList();
    }

    /**
     * 修改标签名称。
     */
    @Override
    @Transactional
    public void updateTag(Long id, UpdateTagDTO request) {
        // 加载需要修改的标签。
        KbTag tag = tagRepository.findById(id)
                .orElseThrow(() -> new BusinessException("标签不存在"));
        String name = request.getName().strip();

        // 排除当前标签后检查同库重名。
        if (tagRepository.existsByKnowledgeBaseIdAndNameAndIdNot(
                tag.getKnowledgeBase().getId(), name, id)) {
            throw new BusinessException("知识库内已存在同名标签");
        }

        tag.setName(name);
    }

    /**
     * 删除未被文档使用的标签。
     */
    @Override
    @Transactional
    public void deleteTag(Long id) {
        // 加载标签，避免把不存在的删除当作成功。
        KbTag tag = tagRepository.findById(id)
                .orElseThrow(() -> new BusinessException("标签不存在"));

        // 检查文档标签关系表中的引用。
        if (documentRepository.existsByTagsId(id)) {
            throw new BusinessException("标签仍被文档使用，不能删除");
        }

        tagRepository.delete(tag);
    }

    private TagVO toTagVO(KbTag tag) {
        return new TagVO(
                tag.getId(),
                tag.getKnowledgeBase().getId(),
                tag.getName(),
                tag.getCreatedTime(),
                tag.getUpdatedTime()
        );
    }
}
