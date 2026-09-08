package com.software.knowledgehub.system.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserQueryDTO {

    @Size(max = 50, message = "用户名长度不能超过50个字符")
    private String username;

    @Size(max = 100, message = "显示名称长度不能超过100个字符")
    private String displayName;

    @Size(max = 255, message = "邮箱长度不能超过255个字符")
    private String email;

    @Min(value = 0, message = "用户状态只能是0或1")
    @Max(value = 1, message = "用户状态只能是0或1")
    private Short status;
}
