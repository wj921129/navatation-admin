package com.navatation.business.dto.resp.auth;

import lombok.Data;
import com.navatation.business.dto.resp.user.UserRespDTO;
import com.navatation.business.dto.resp.auth.LoginRespDTO;

/**
 * 用户登录响应结果
 *
 * @date 2026-06-09
 */
@Data
public class LoginRespDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserRespDTO userInfo;
}
