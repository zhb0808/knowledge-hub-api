package com.software.knowledgehub.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePermissionDTO {

    @NotBlank
    private String name;

    private String menuPath;

    @NotBlank
    private String apiRules;

    private Long parentId;

    @NotNull
    private Integer sortOrder;
}
