package com.navatation.business.dto.req.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.auth.RefreshTokenReqDTO;

/**
 * 刷新Token请求参数
 *
 * @date 2026-06-09
 */
@Data
public class RefreshTokenReqDTO {
    @NotBlank
    private String refreshToken;
}
