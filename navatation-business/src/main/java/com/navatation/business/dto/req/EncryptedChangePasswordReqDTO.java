package com.navatation.business.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 加密修改密码请求
 * 密码通过 RSA 加密后传输，用户身份通过 JWT 鉴权确定
 */
@Data
public class EncryptedChangePasswordReqDTO {

    @NotBlank(message = "加密数据不能为空")
    private String encryptedData;

    @NotBlank(message = "nonce 不能为空")
    private String nonce;
}
