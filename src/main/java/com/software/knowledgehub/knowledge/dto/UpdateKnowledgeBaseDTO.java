package com.software.knowledgehub.knowledge.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateKnowledgeBaseDTO {

    @NotBlank(message = "知识库名称不能为空")
    @Size(max = 100, message = "知识库名称长度不能超过100个字符")
    private String name;

    @Size(max = 500, message = "知识库描述长度不能超过500个字符")
    private String description;

    @NotNull(message = "知识库状态不能为空")
    @Min(value = 0, message = "知识库状态只能是0或1")
    @Max(value = 1, message = "知识库状态只能是0或1")
    private Short status;
}
