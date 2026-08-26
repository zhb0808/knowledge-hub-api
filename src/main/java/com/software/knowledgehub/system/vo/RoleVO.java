package com.software.knowledgehub.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleVO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Set<Long> permissionIds;
    private OffsetDateTime createdTime;
    private OffsetDateTime updatedTime;
}
