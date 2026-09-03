package com.software.knowledgehub.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AiConversationDTO {

    @NotBlank(message = "对话ID不能为空")
    @Size(max = 64, message = "对话ID不能超过64个字符")
    private String conversationId;

    @NotBlank(message = "问题不能为空")
    @Size(max = 2000, message = "问题不能超过2000个字符")
    private String message;
}
