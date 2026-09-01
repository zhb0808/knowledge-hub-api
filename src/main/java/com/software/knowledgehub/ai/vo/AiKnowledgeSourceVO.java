package com.software.knowledgehub.ai.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiKnowledgeSourceVO {

    private Long documentId;

    private String title;

    private String content;
}
