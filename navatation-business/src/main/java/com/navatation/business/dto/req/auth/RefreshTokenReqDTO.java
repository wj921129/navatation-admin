package com.navatation.business.dto.req.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.auth.RefreshTokenReqDTO;

/**
 * RefreshTokenReqDTO 功能描述
 *
 * @date 2026-06-09
 */
@Data
public class RefreshTokenReqDTO {
    @NotBlank
    private String refreshToken;
}
