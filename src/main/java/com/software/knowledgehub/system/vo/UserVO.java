package com.software.knowledgehub.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private Long id;
    private String username;
    private String displayName;
    private String email;
    private Short status;
    private Set<Long> roleIds;
    private OffsetDateTime createdTime;
    private OffsetDateTime updatedTime;
}
