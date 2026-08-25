package com.software.knowledgehub.knowledge.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCategoryDTO {

    @NotNull(message = "知识库ID不能为空")
    @Positive(message = "知识库ID必须大于0")
    private Long knowledgeBaseId;

    @Positive(message = "父分类ID必须大于0")
    private Long parentId;

    @NotBlank(message = "分类名称不能为空")
    @Size(max = 100, message = "分类名称长度不能超过100个字符")
    private String name;

    @NotNull(message = "分类排序值不能为空")
    @Min(value = 0, message = "分类排序值不能小于0")
    private Integer sortOrder;
}
