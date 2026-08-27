package com.software.knowledgehub.knowledge.repository;

import com.software.knowledgehub.knowledge.entity.KbFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KbFileRepository extends JpaRepository<KbFile, Long> {

    Optional<KbFile> findByDocumentId(Long documentId);
}
