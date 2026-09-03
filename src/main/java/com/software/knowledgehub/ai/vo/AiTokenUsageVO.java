package com.software.knowledgehub.ai.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiTokenUsageVO {

    private Integer inputTokens;

    private Integer outputTokens;

    private Integer totalTokens;
}
