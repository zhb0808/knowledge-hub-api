package com.software.knowledgehub.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBasePreviewDTO {

    @NotBlank(message = "知识库编码不能为空")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]{0,49}$",
            message = "知识库编码必须以字母开头，只能包含字母、数字和下划线，长度不能超过50个字符"
    )
    private String code;

    @NotBlank(message = "知识库名称不能为空")
    @Size(max = 100, message = "知识库名称长度不能超过100个字符")
    private String name;

    @Size(max = 500, message = "知识库描述长度不能超过500个字符")
    private String description;
}
