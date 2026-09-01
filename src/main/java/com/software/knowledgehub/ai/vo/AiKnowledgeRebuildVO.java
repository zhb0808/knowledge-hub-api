package com.software.knowledgehub.ai.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiKnowledgeRebuildVO {

    private int documentCount;

    private int chunkCount;
}
