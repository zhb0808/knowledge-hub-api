package com.software.knowledgehub.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class AssignPermissionDTO {

    @NotNull
    private Set<Long> permissionIds;
}
