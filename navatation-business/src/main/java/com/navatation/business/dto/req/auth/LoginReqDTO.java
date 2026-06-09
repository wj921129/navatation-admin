package com.navatation.business.dto.req.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.auth.LoginReqDTO;

/**
 * 用户登录请求参数
 *
 * @date 2026-06-09
 */
@Data
public class LoginReqDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
