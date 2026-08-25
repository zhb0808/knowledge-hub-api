package com.software.knowledgehub.knowledge.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DocumentQueryDTO {

    @NotNull(message = "知识库ID不能为空")
    @Positive(message = "知识库ID必须大于0")
    private Long knowledgeBaseId;

    @Size(max = 100, message = "关键词长度不能超过100个字符")
    private String keyword;

    @Positive(message = "分类ID必须大于0")
    private Long categoryId;

    @Positive(message = "标签ID必须大于0")
    private Long tagId;

    @Pattern(
            regexp = "DRAFT|PUBLISHED|ARCHIVED",
            message = "文档状态只能是DRAFT、PUBLISHED或ARCHIVED"
    )
    private String status;
}
