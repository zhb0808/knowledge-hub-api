package com.software.knowledgehub.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateRoleDTO {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;
}
