package com.software.knowledgehub.knowledge.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KnowledgeBaseQueryDTO {

    @Size(max = 50, message = "知识库编码长度不能超过50个字符")
    private String code;

    @Size(max = 100, message = "知识库名称长度不能超过100个字符")
    private String name;

    @Min(value = 0, message = "知识库状态只能是0或1")
    @Max(value = 1, message = "知识库状态只能是0或1")
    private Short status;
}
