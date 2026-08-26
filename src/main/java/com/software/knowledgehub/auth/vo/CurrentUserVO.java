package com.software.knowledgehub.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUserVO {

    private Long id;
    private String username;
    private String displayName;
}
