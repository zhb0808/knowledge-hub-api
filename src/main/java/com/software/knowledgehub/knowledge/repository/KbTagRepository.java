package com.software.knowledgehub.knowledge.repository;

import com.software.knowledgehub.knowledge.entity.KbTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface KbTagRepository extends JpaRepository<KbTag, Long> {

    List<KbTag> findByKnowledgeBaseIdOrderByNameAsc(Long knowledgeBaseId);

    boolean existsByKnowledgeBaseIdAndName(Long knowledgeBaseId, String name);

    boolean existsByKnowledgeBaseIdAndNameAndIdNot(
            Long knowledgeBaseId,
            String name,
            Long id);

    List<KbTag> findAllByKnowledgeBaseIdAndIdIn(
            Long knowledgeBaseId,
            Collection<Long> ids);

    boolean existsByKnowledgeBaseId(Long knowledgeBaseId);
}
