package com.software.knowledgehub.knowledge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVO {

    private Long id;
    private Long knowledgeBaseId;
    private Long parentId;
    private String name;
    private Integer sortOrder;
    private OffsetDateTime createdTime;
    private OffsetDateTime updatedTime;
}
