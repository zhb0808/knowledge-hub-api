package com.software.knowledgehub.knowledge.service.impl;

import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.knowledge.dto.CreateCategoryDTO;
import com.software.knowledgehub.knowledge.dto.UpdateCategoryDTO;
import com.software.knowledgehub.knowledge.entity.KbCategory;
import com.software.knowledgehub.knowledge.entity.KbKnowledgeBase;
import com.software.knowledgehub.knowledge.repository.KbCategoryRepository;
import com.software.knowledgehub.knowledge.repository.KbDocumentRepository;
import com.software.knowledgehub.knowledge.repository.KbKnowledgeBaseRepository;
import com.software.knowledgehub.knowledge.service.CategoryService;
import com.software.knowledgehub.knowledge.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final KbCategoryRepository categoryRepository;
    private final KbKnowledgeBaseRepository knowledgeBaseRepository;
    private final KbDocumentRepository documentRepository;

    /**
     * 创建分类。
     */
    @Override
    @Transactional
    public CategoryVO createCategory(CreateCategoryDTO request) {
        // 加载分类所属的知识库。
        KbKnowledgeBase knowledgeBase = knowledgeBaseRepository
                .findById(request.getKnowledgeBaseId())
                .orElseThrow(() -> new BusinessException("知识库不存在"));
        KbCategory parent = loadParent(request.getParentId(), knowledgeBase.getId());

        KbCategory category = new KbCategory();
        category.setKnowledgeBase(knowledgeBase);
        category.setParent(parent);
        category.setName(request.getName().strip());
        category.setSortOrder(request.getSortOrder());

        // 保存分类及其父级关系。
        return toCategoryVO(categoryRepository.save(category));
    }

    /**
     * 查询分类详情。
     */
    @Override
    public CategoryVO getCategory(Long id) {
        // 按主键加载分类。
        KbCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        return toCategoryVO(category);
    }

    /**
     * 查询知识库的分类列表。
     */
    @Override
    public List<CategoryVO> listCategories(Long knowledgeBaseId) {
        // 先确认知识库存在，区分空列表与无效知识库。
        if (!knowledgeBaseRepository.existsById(knowledgeBaseId)) {
            throw new BusinessException("知识库不存在");
        }

        // 按排序值加载分类。
        return categoryRepository
                .findByKnowledgeBaseIdOrderBySortOrderAsc(knowledgeBaseId)
                .stream()
                .map(this::toCategoryVO)
                .toList();
    }

    /**
     * 修改分类及其父级关系。
     */
    @Override
    @Transactional
    public void updateCategory(Long id, UpdateCategoryDTO request) {
        // 加载需要修改的分类。
        KbCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        KbCategory parent = loadParent(
                request.getParentId(),
                category.getKnowledgeBase().getId()
        );

        // 沿父级链检查自引用和间接循环。
        KbCategory current = parent;
        while (current != null) {
            if (current.getId().equals(category.getId())) {
                throw new BusinessException("分类父级关系不能形成循环");
            }
            current = current.getParent();
        }

        category.setParent(parent);
        category.setName(request.getName().strip());
        category.setSortOrder(request.getSortOrder());
    }

    /**
     * 删除未被引用的分类。
     */
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        // 加载分类，避免把不存在的删除当作成功。
        KbCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException("分类不存在"));

        // 检查子分类和文档对当前分类的引用。
        if (categoryRepository.existsByParentId(id)
                || documentRepository.existsByCategoryId(id)) {
            throw new BusinessException("分类仍有子分类或关联文档，不能删除");
        }

        categoryRepository.delete(category);
    }

    private KbCategory loadParent(Long parentId, Long knowledgeBaseId) {
        if (parentId == null) {
            return null;
        }

        // 加载父分类并校验知识库归属。
        KbCategory parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException("父分类不存在"));
        if (!parent.getKnowledgeBase().getId().equals(knowledgeBaseId)) {
            throw new BusinessException("父分类必须属于同一个知识库");
        }
        return parent;
    }

    private CategoryVO toCategoryVO(KbCategory category) {
        Long parentId = category.getParent() == null
                ? null
                : category.getParent().getId();
        return new CategoryVO(
                category.getId(),
                category.getKnowledgeBase().getId(),
                parentId,
                category.getName(),
                category.getSortOrder(),
                category.getCreatedTime(),
                category.getUpdatedTime()
        );
    }
}
