package com.software.knowledgehub.ai.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiKnowledgeChatVO {

    private String answer;

    private List<AiKnowledgeSourceVO> sources;
}
