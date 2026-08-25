package com.software.knowledgehub.knowledge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBaseVO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Short status;
    private OffsetDateTime createdTime;
    private OffsetDateTime updatedTime;
}
