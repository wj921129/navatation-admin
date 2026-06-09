package com.navatation.business.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordReqDTO {
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 32, message = "新密码长度必须在6到32个字符之间")
    private String newPassword;

    @NotBlank(message = "确认新密码不能为空")
    private String confirmPassword;
}
