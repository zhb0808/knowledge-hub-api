package com.software.knowledgehub.system.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleQueryDTO {

    @Size(max = 50, message = "角色编码长度不能超过50个字符")
    private String code;

    @Size(max = 100, message = "角色名称长度不能超过100个字符")
    private String name;
}
