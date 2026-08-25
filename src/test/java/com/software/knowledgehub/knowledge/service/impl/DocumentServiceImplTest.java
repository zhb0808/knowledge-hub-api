package com.software.knowledgehub.knowledge.service.impl;

import com.software.knowledgehub.common.exception.BusinessException;
import com.software.knowledgehub.knowledge.dto.CreateDocumentDTO;
import com.software.knowledgehub.knowledge.dto.UpdateDocumentDTO;
import com.software.knowledgehub.knowledge.entity.KbDocument;
import com.software.knowledgehub.knowledge.entity.KbKnowledgeBase;
import com.software.knowledgehub.knowledge.entity.KbTag;
import com.software.knowledgehub.knowledge.repository.KbCategoryRepository;
import com.software.knowledgehub.knowledge.repository.KbDocumentRepository;
import com.software.knowledgehub.knowledge.repository.KbKnowledgeBaseRepository;
import com.software.knowledgehub.knowledge.repository.KbTagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private KbDocumentRepository documentRepository;

    @Mock
    private KbKnowledgeBaseRepository knowledgeBaseRepository;

    @Mock
    private KbCategoryRepository categoryRepository;

    @Mock
    private KbTagRepository tagRepository;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @Test
    void shouldCreateDocumentAndBindTags() {
        KbKnowledgeBase knowledgeBase = new KbKnowledgeBase();
        knowledgeBase.setId(1L);
        KbTag javaTag = createTag(10L, "Java", knowledgeBase);
        KbTag jpaTag = createTag(11L, "JPA", knowledgeBase);

        CreateDocumentDTO request = new CreateDocumentDTO();
        request.setKnowledgeBaseId(1L);
        request.setTitle(" JPA开发规范 ");
        request.setTagIds(Set.of(10L, 11L));

        when(knowledgeBaseRepository.findById(1L))
                .thenReturn(Optional.of(knowledgeBase));
        when(tagRepository.findAllByKnowledgeBase_IdAndIdIn(
                1L, request.getTagIds()))
                .thenReturn(List.of(javaTag, jpaTag));
        when(documentRepository.save(any(KbDocument.class))).thenAnswer(invocation -> {
            KbDocument document = invocation.getArgument(0);
            document.setId(20L);
            return document;
        });

        documentService.createDocument(request);

        ArgumentCaptor<KbDocument> captor = ArgumentCaptor.forClass(KbDocument.class);
        verify(documentRepository).save(captor.capture());
        assertThat(captor.getValue().getTitle()).isEqualTo("JPA开发规范");
        assertThat(captor.getValue().getStatus()).isEqualTo("DRAFT");
        assertThat(captor.getValue().getTags()).containsExactlyInAnyOrder(javaTag, jpaTag);
    }

    @Test
    void shouldRejectTagOutsideDocumentKnowledgeBase() {
        KbKnowledgeBase knowledgeBase = new KbKnowledgeBase();
        knowledgeBase.setId(1L);
        CreateDocumentDTO request = new CreateDocumentDTO();
        request.setKnowledgeBaseId(1L);
        request.setTitle("JPA开发规范");
        request.setTagIds(Set.of(10L, 11L));

        when(knowledgeBaseRepository.findById(1L))
                .thenReturn(Optional.of(knowledgeBase));
        when(tagRepository.findAllByKnowledgeBase_IdAndIdIn(
                1L, request.getTagIds()))
                .thenReturn(List.of(createTag(10L, "Java", knowledgeBase)));

        assertThatThrownBy(() -> documentService.createDocument(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("存在无效标签或标签不属于文档所在知识库");
    }

    @Test
    void shouldReplaceManagedDocumentTagsWithoutCallingSave() {
        KbKnowledgeBase knowledgeBase = new KbKnowledgeBase();
        knowledgeBase.setId(1L);
        KbTag oldTag = createTag(10L, "Java", knowledgeBase);
        KbTag newTag = createTag(11L, "JPA", knowledgeBase);
        KbDocument document = new KbDocument();
        document.setId(20L);
        document.setKnowledgeBase(knowledgeBase);
        document.getTags().add(oldTag);

        UpdateDocumentDTO request = new UpdateDocumentDTO();
        request.setTitle("JPA开发规范");
        request.setStatus("PUBLISHED");
        request.setTagIds(Set.of(11L));

        when(documentRepository.findById(20L)).thenReturn(Optional.of(document));
        when(tagRepository.findAllByKnowledgeBase_IdAndIdIn(
                1L, request.getTagIds()))
                .thenReturn(List.of(newTag));

        documentService.updateDocument(20L, request);

        assertThat(document.getTags()).containsExactly(newTag);
        assertThat(document.getStatus()).isEqualTo("PUBLISHED");
        verify(documentRepository, never()).save(any(KbDocument.class));
    }

    private KbTag createTag(Long id, String name, KbKnowledgeBase knowledgeBase) {
        KbTag tag = new KbTag();
        tag.setId(id);
        tag.setName(name);
        tag.setKnowledgeBase(knowledgeBase);
        return tag;
    }
}
