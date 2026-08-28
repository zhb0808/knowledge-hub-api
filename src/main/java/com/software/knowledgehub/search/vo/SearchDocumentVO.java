package com.software.knowledgehub.search.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchDocumentVO {

    private Long id;
    private Long knowledgeBaseId;
    private String knowledgeBaseName;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String summary;
    private OffsetDateTime updatedTime;
}
