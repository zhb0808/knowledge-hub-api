package com.software.knowledgehub.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTagDTO {

    @NotNull(message = "知识库ID不能为空")
    @Positive(message = "知识库ID必须大于0")
    private Long knowledgeBaseId;

    @NotBlank(message = "标签名称不能为空")
    @Size(max = 50, message = "标签名称长度不能超过50个字符")
    private String name;
}
