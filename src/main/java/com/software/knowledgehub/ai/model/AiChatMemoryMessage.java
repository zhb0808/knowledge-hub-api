package com.software.knowledgehub.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatMemoryMessage {

    private String type;

    private String content;
}
