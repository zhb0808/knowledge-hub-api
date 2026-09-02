package com.software.knowledgehub.ai.model;

import com.software.knowledgehub.ai.vo.AiKnowledgeSourceVO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AiKnowledgeContext {

    private final String userPrompt;
    private final List<AiKnowledgeSourceVO> sources;
}
