package com.software.knowledgehub.knowledge.service.impl;

import com.software.knowledgehub.common.exception.ConflictException;
import com.software.knowledgehub.knowledge.dto.CreateKnowledgeBaseDTO;
import com.software.knowledgehub.knowledge.dto.UpdateKnowledgeBaseDTO;
import com.software.knowledgehub.knowledge.entity.KbKnowledgeBase;
import com.software.knowledgehub.knowledge.repository.KbCategoryRepository;
import com.software.knowledgehub.knowledge.repository.KbDocumentRepository;
import com.software.knowledgehub.knowledge.repository.KbKnowledgeBaseRepository;
import com.software.knowledgehub.knowledge.repository.KbTagRepository;
import com.software.knowledgehub.knowledge.vo.KnowledgeBaseVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KnowledgeBaseServiceImplTest {

    @Mock
    private KbKnowledgeBaseRepository knowledgeBaseRepository;

    @Mock
    private KbCategoryRepository categoryRepository;

    @Mock
    private KbDocumentRepository documentRepository;

    @Mock
    private KbTagRepository tagRepository;

    @InjectMocks
    private KnowledgeBaseServiceImpl knowledgeBaseService;

    @Test
    void shouldCreateKnowledgeBaseWithNormalizedCode() {
        CreateKnowledgeBaseDTO request = new CreateKnowledgeBaseDTO();
        request.setCode("tech_docs");
        request.setName(" 技术知识库 ");
        request.setDescription(" 开发规范 ");
        when(knowledgeBaseRepository.existsByCode("TECH_DOCS")).thenReturn(false);
        when(knowledgeBaseRepository.save(any(KbKnowledgeBase.class))).thenAnswer(invocation -> {
            KbKnowledgeBase knowledgeBase = invocation.getArgument(0);
            knowledgeBase.setId(2L);
            return knowledgeBase;
        });

        KnowledgeBaseVO result = knowledgeBaseService.createKnowledgeBase(request);

        ArgumentCaptor<KbKnowledgeBase> captor = ArgumentCaptor.forClass(KbKnowledgeBase.class);
        verify(knowledgeBaseRepository).save(captor.capture());
        assertThat(captor.getValue().getCode()).isEqualTo("TECH_DOCS");
        assertThat(result.getCode()).isEqualTo("TECH_DOCS");
    }

    @Test
    void shouldUpdateManagedKnowledgeBaseWithoutCallingSave() {
        KbKnowledgeBase knowledgeBase = new KbKnowledgeBase();
        knowledgeBase.setId(2L);
        knowledgeBase.setName("旧名称");
        knowledgeBase.setStatus((short) 1);

        UpdateKnowledgeBaseDTO request = new UpdateKnowledgeBaseDTO();
        request.setName("新名称");
        request.setStatus((short) 0);

        when(knowledgeBaseRepository.findById(2L)).thenReturn(Optional.of(knowledgeBase));

        knowledgeBaseService.updateKnowledgeBase(2L, request);

        assertThat(knowledgeBase.getName()).isEqualTo("新名称");
        assertThat(knowledgeBase.getStatus()).isZero();
        verify(knowledgeBaseRepository, never()).save(any(KbKnowledgeBase.class));
    }

    @Test
    void shouldRejectDeletingKnowledgeBaseWithCategories() {
        KbKnowledgeBase knowledgeBase = new KbKnowledgeBase();
        knowledgeBase.setId(2L);

        when(knowledgeBaseRepository.findById(2L)).thenReturn(Optional.of(knowledgeBase));
        when(categoryRepository.existsByKnowledgeBase_Id(2L)).thenReturn(true);

        assertThatThrownBy(() -> knowledgeBaseService.deleteKnowledgeBase(2L))
                .isInstanceOf(ConflictException.class)
                .hasMessage("知识库下仍有分类、文档或标签，不能删除");

        verify(knowledgeBaseRepository, never()).delete(any(KbKnowledgeBase.class));
    }
}
