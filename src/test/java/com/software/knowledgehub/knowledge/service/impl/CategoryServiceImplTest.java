package com.software.knowledgehub.knowledge.service.impl;

import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.common.exception.ConflictException;
import com.software.knowledgehub.knowledge.dto.CreateCategoryDTO;
import com.software.knowledgehub.knowledge.dto.UpdateCategoryDTO;
import com.software.knowledgehub.knowledge.entity.KbCategory;
import com.software.knowledgehub.knowledge.entity.KbKnowledgeBase;
import com.software.knowledgehub.knowledge.repository.KbCategoryRepository;
import com.software.knowledgehub.knowledge.repository.KbDocumentRepository;
import com.software.knowledgehub.knowledge.repository.KbKnowledgeBaseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private KbCategoryRepository categoryRepository;

    @Mock
    private KbKnowledgeBaseRepository knowledgeBaseRepository;

    @Mock
    private KbDocumentRepository documentRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void shouldRejectParentFromAnotherKnowledgeBase() {
        KbKnowledgeBase knowledgeBase = new KbKnowledgeBase();
        knowledgeBase.setId(1L);
        KbKnowledgeBase anotherKnowledgeBase = new KbKnowledgeBase();
        anotherKnowledgeBase.setId(2L);
        KbCategory parent = new KbCategory();
        parent.setId(10L);
        parent.setKnowledgeBase(anotherKnowledgeBase);

        CreateCategoryDTO request = new CreateCategoryDTO();
        request.setKnowledgeBaseId(1L);
        request.setParentId(10L);
        request.setName("开发规范");
        request.setSortOrder(1);

        when(knowledgeBaseRepository.findById(1L))
                .thenReturn(Optional.of(knowledgeBase));
        when(categoryRepository.findById(10L)).thenReturn(Optional.of(parent));

        assertThatThrownBy(() -> categoryService.createCategory(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("父分类必须属于同一个知识库");
    }

    @Test
    void shouldRejectCircularParentRelationship() {
        KbKnowledgeBase knowledgeBase = new KbKnowledgeBase();
        knowledgeBase.setId(1L);
        KbCategory category = new KbCategory();
        category.setId(10L);
        category.setKnowledgeBase(knowledgeBase);
        KbCategory child = new KbCategory();
        child.setId(11L);
        child.setKnowledgeBase(knowledgeBase);
        child.setParent(category);

        UpdateCategoryDTO request = new UpdateCategoryDTO();
        request.setParentId(11L);
        request.setName("开发规范");
        request.setSortOrder(1);

        when(categoryRepository.findById(10L)).thenReturn(Optional.of(category));
        when(categoryRepository.findById(11L)).thenReturn(Optional.of(child));

        assertThatThrownBy(() -> categoryService.updateCategory(10L, request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("分类父级关系不能形成循环");
    }
}
