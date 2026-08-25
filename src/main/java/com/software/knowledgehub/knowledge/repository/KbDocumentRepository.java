package com.software.knowledgehub.knowledge.repository;

import com.software.knowledgehub.knowledge.entity.KbDocument;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface KbDocumentRepository extends JpaRepository<KbDocument, Long>, JpaSpecificationExecutor<KbDocument> {

    @EntityGraph(attributePaths = {"category", "tags"})
    List<KbDocument> findByIdIn(Collection<Long> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update KbDocument document
            set document.status = :status
            where document.knowledgeBase.id = :knowledgeBaseId
            and document.id in :ids
            """)
    int updateStatusByKnowledgeBaseIdAndIdIn(
            @Param("knowledgeBaseId") Long knowledgeBaseId,
            @Param("ids") Collection<Long> ids,
            @Param("status") String status);

    boolean existsByKnowledgeBaseId(Long knowledgeBaseId);

    boolean existsByCategoryId(Long categoryId);

    boolean existsByTagsId(Long tagId);
}
