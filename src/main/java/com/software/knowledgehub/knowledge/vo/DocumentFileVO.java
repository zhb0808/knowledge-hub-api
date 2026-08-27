package com.software.knowledgehub.knowledge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentFileVO {

    private Long id;
    private Long documentId;
    private String originalName;
    private String contentType;
    private Long fileSize;
    private OffsetDateTime createdTime;
    private OffsetDateTime updatedTime;
}
