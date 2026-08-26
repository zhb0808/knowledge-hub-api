package com.software.knowledgehub.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserDTO {

    @NotBlank(message = "用户名不能为空")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]{0,49}$",
            message = "用户名必须以字母开头，只能包含字母、数字和下划线，长度不能超过50个字符"
    )
    private String username;

    @NotBlank(message = "显示名称不能为空")
    @Size(max = 100, message = "显示名称长度不能超过100个字符")
    private String displayName;

    @NotBlank(message = "密码不能为空")
    private String password;

    @Email(message = "邮箱格式不正确")
    @Size(max = 255, message = "邮箱长度不能超过255个字符")
    private String email;
}
