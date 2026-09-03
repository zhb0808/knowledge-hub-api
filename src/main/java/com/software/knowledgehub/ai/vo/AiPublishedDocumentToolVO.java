package com.software.knowledgehub.ai.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiPublishedDocumentToolVO {

    private Long id;

    private String title;

    private String summary;

    private String knowledgeBaseName;

    private OffsetDateTime updatedTime;
}
