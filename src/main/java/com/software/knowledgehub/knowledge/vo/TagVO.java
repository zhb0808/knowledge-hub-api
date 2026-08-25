package com.software.knowledgehub.knowledge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagVO {

    private Long id;
    private Long knowledgeBaseId;
    private String name;
    private OffsetDateTime createdTime;
    private OffsetDateTime updatedTime;
}
