package com.software.knowledgehub.knowledge.service;

import com.software.knowledgehub.knowledge.dto.CreateCategoryDTO;
import com.software.knowledgehub.knowledge.dto.UpdateCategoryDTO;
import com.software.knowledgehub.knowledge.vo.CategoryVO;

import java.util.List;

public interface CategoryService {

    /**
     * 创建分类。
     */
    CategoryVO createCategory(CreateCategoryDTO request);

    /**
     * 查询分类详情。
     */
    CategoryVO getCategory(Long id);

    /**
     * 查询知识库的分类列表。
     */
    List<CategoryVO> listCategories(Long knowledgeBaseId);

    /**
     * 修改分类及其父级关系。
     */
    void updateCategory(Long id, UpdateCategoryDTO request);

    /**
     * 删除未被引用的分类。
     */
    void deleteCategory(Long id);
}
