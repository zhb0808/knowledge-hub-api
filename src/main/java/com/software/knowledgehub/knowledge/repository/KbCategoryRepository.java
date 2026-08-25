package com.software.knowledgehub.knowledge.repository;

import com.software.knowledgehub.knowledge.entity.KbCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KbCategoryRepository extends JpaRepository<KbCategory, Long> {

    List<KbCategory> findByKnowledgeBaseIdOrderBySortOrderAsc(Long knowledgeBaseId);

    boolean existsByKnowledgeBaseId(Long knowledgeBaseId);

    boolean existsByParentId(Long parentId);
}
