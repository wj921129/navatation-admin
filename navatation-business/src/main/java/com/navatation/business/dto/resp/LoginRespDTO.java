package com.navatation.business.dto.resp;

import lombok.Data;

@Data
public class LoginRespDTO {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserRespDTO userInfo;
}
