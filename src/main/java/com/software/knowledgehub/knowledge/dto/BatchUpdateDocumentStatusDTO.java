package com.software.knowledgehub.knowledge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class BatchUpdateDocumentStatusDTO {

    @NotNull(message = "知识库ID不能为空")
    @Positive(message = "知识库ID必须大于0")
    private Long knowledgeBaseId;

    @NotEmpty(message = "文档ID集合不能为空")
    private Set<@Positive(message = "文档ID必须大于0") Long> documentIds = new LinkedHashSet<>();

    @NotBlank(message = "文档状态不能为空")
    @Pattern(
            regexp = "DRAFT|PUBLISHED|ARCHIVED",
            message = "文档状态只能是DRAFT、PUBLISHED或ARCHIVED"
    )
    private String status;
}
