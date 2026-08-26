package com.software.knowledgehub.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionVO {

    private Long id;
    private String code;
    private String name;
    private String menuPath;
    private String apiRules;
    private Long parentId;
    private Integer sortOrder;
    private OffsetDateTime createdTime;
    private OffsetDateTime updatedTime;
}
