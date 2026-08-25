package com.software.knowledgehub.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class CreateDocumentDTO {

    @NotNull(message = "知识库ID不能为空")
    @Positive(message = "知识库ID必须大于0")
    private Long knowledgeBaseId;

    @Positive(message = "分类ID必须大于0")
    private Long categoryId;

    @NotBlank(message = "文档标题不能为空")
    @Size(max = 200, message = "文档标题长度不能超过200个字符")
    private String title;

    @Size(max = 500, message = "文档摘要长度不能超过500个字符")
    private String summary;

    private String content;

    @NotNull(message = "标签ID集合不能为空")
    private Set<@Positive(message = "标签ID必须大于0") Long> tagIds = new LinkedHashSet<>();
}
