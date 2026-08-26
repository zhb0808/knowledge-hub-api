package com.software.knowledgehub.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateRoleDTO {

    @NotBlank
    private String name;

    private String description;
}
