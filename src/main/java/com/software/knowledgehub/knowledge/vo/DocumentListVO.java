package com.software.knowledgehub.knowledge.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentListVO {

    private Long id;
    private Long knowledgeBaseId;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String summary;
    private String status;
    private List<TagVO> tags;
    private OffsetDateTime createdTime;
    private OffsetDateTime updatedTime;
}
