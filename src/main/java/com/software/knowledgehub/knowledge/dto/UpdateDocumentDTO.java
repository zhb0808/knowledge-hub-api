package com.software.knowledgehub.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class UpdateDocumentDTO {

    @Positive(message = "分类ID必须大于0")
    private Long categoryId;

    @NotBlank(message = "文档标题不能为空")
    @Size(max = 200, message = "文档标题长度不能超过200个字符")
    private String title;

    @Size(max = 500, message = "文档摘要长度不能超过500个字符")
    private String summary;

    private String content;

    @NotBlank(message = "文档状态不能为空")
    @Pattern(
            regexp = "DRAFT|PUBLISHED|ARCHIVED",
            message = "文档状态只能是DRAFT、PUBLISHED或ARCHIVED"
    )
    private String status;

    @NotNull(message = "标签ID集合不能为空")
    private Set<@Positive(message = "标签ID必须大于0") Long> tagIds = new LinkedHashSet<>();
}
