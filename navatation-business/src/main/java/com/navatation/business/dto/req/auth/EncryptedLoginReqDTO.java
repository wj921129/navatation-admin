package com.navatation.business.dto.req.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.auth.EncryptedLoginReqDTO;

/**
 * 加密登录请求
 * username 明文传输，密码通过 RSA-OAEP/SHA-256 (MGF1 SHA-256) 加密后传输
 */
@Data
public class EncryptedLoginReqDTO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "加密数据不能为空")
    private String encryptedData;

    @NotBlank(message = "nonce 不能为空")
    private String nonce;
}
