package com.software.knowledgehub.search.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SearchDocumentQueryDTO {

    @NotBlank(message = "搜索关键词不能为空")
    @Size(max = 100, message = "搜索关键词长度不能超过100个字符")
    private String keyword;
}
