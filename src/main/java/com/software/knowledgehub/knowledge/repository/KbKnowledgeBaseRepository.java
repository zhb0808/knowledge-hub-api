package com.software.knowledgehub.knowledge.repository;

import com.software.knowledgehub.knowledge.entity.KbKnowledgeBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface KbKnowledgeBaseRepository extends
        JpaRepository<KbKnowledgeBase, Long>,
        JpaSpecificationExecutor<KbKnowledgeBase> {

    Optional<KbKnowledgeBase> findByCode(String code);

    boolean existsByCode(String code);

}
