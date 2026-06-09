package com.navatation.business.dto.req.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.auth.LoginReqDTO;

/**
 * LoginReqDTO 功能描述
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
