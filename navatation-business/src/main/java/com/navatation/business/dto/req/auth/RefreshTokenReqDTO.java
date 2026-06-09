package com.navatation.business.dto.req.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.auth.RefreshTokenReqDTO;

@Data
public class RefreshTokenReqDTO {
    @NotBlank
    private String refreshToken;
}
