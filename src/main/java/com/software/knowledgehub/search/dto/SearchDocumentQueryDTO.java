package com.software.knowledgehub.search.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SearchDocumentQueryDTO {

    @NotBlank(message = "搜索关键词不能为空")
    @Size(max = 100, message = "搜索关键词长度不能超过100个字符")
    private String keyword;

    @Positive(message = "知识库ID必须为正整数")
    private Long knowledgeBaseId;

    @Positive(message = "分类ID必须为正整数")
    private Long categoryId;

    @Positive(message = "标签ID必须为正整数")
    private Long tagId;
}
