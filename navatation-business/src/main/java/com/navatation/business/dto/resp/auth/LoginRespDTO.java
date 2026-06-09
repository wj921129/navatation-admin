package com.navatation.business.dto.resp.auth;

import lombok.Data;
import com.navatation.business.dto.resp.user.UserRespDTO;
import com.navatation.business.dto.resp.auth.LoginRespDTO;

@Data
public class LoginRespDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserRespDTO userInfo;
}
