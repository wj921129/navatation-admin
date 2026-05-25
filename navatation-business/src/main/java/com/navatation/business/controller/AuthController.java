package com.navatation.business.controller;

import com.navatation.business.dto.ChangePasswordRequest;
import com.navatation.business.dto.EncryptedChangePasswordRequest;
import com.navatation.business.dto.EncryptedLoginRequest;
import com.navatation.business.dto.EncryptedRegisterRequest;
import com.navatation.business.dto.LoginRequest;
import com.navatation.business.dto.LoginVO;
import com.navatation.business.dto.RefreshTokenRequest;
import com.navatation.business.dto.RegisterRequest;
import com.navatation.business.dto.UserVO;
import com.navatation.business.service.AuthService;
import com.navatation.common.BizException;
import com.navatation.common.Result;
import com.navatation.common.ResultCode;
import com.navatation.framework.security.JwtTokenProvider;
import com.navatation.framework.security.NonceService;
import com.navatation.framework.security.RsaKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description 认证控制器，处理注册、登录、登出、Token刷新及当前用户查询 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RsaKeyService rsaKeyService;
    private final NonceService nonceService;

    /**
     * 获取一次性 nonce 和 RSA 公钥
     * 前端在登录/注册/改密前，先请求此接口获取 nonce 和公钥
     * @return { nonce, publicKey }
     */
    @GetMapping("/nonce")
    public Result<Map<String, String>> getNonce() {
        String nonce = nonceService.generateNonce();
        String publicKey = rsaKeyService.getPublicKeyPem();
        return Result.success(Map.of("nonce", nonce, "publicKey", publicKey));
    }

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody EncryptedRegisterRequest req) {
        log.info("用户注册 入参:username={}", req.getUsername());

        // 1. 校验并消费 nonce
        if (!nonceService.consumeNonce(req.getNonce())) {
            throw new BizException(ResultCode.NONCE_INVALID);
        }

        // 2. 解密加密数据
        String decrypted = rsaKeyService.decrypt(req.getEncryptedData());
        String[] parts = decrypted.split("\\|");

        // 3. 校验解密后的 nonce（从右往左取最后一段，避免密码含竖线导致错位）
        String decryptNonce = parts[parts.length - 1];
        if (!req.getNonce().equals(decryptNonce)) {
            throw new BizException(ResultCode.NONCE_INVALID);
        }

        // 4. 提取密码字段：password|confirmPassword|nonce
        String password = parts[0];
        String confirmPassword = parts[1];

        // 5. 构造旧 DTO 调用 service
        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setUsername(req.getUsername());
        registerReq.setPassword(password);
        registerReq.setConfirmPassword(confirmPassword);
        authService.register(registerReq);

        // 6. 注册成功后自动登录
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername(req.getUsername());
        loginReq.setPassword(password);
        LoginVO loginVO = authService.login(loginReq);

        log.info("用户注册 出参:userId={}", loginVO.getUserInfo().getUserId());
        return Result.success("注册成功", loginVO.getUserInfo());
    }

    @PostMapping("/change-password")
    public Result<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody EncryptedChangePasswordRequest req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(authHeader);
        log.info("用户修改密码 入参:userId={}", userId);

        // 1. 校验并消费 nonce
        if (!nonceService.consumeNonce(req.getNonce())) {
            throw new BizException(ResultCode.NONCE_INVALID);
        }

        // 2. 解密加密数据
        String decrypted = rsaKeyService.decrypt(req.getEncryptedData());
        String[] parts = decrypted.split("\\|");

        // 3. 校验解密后的 nonce
        String decryptNonce = parts[parts.length - 1];
        if (!req.getNonce().equals(decryptNonce)) {
            throw new BizException(ResultCode.NONCE_INVALID);
        }

        // 4. 提取密码字段：oldPassword|newPassword|confirmPassword|nonce
        String oldPassword = parts[0];
        String newPassword = parts[1];
        String confirmPassword = parts[2];

        // 5. 构造旧 DTO 调用 service
        ChangePasswordRequest changeReq = new ChangePasswordRequest();
        changeReq.setOldPassword(oldPassword);
        changeReq.setNewPassword(newPassword);
        changeReq.setConfirmPassword(confirmPassword);
        authService.changePassword(userId, changeReq);

        log.info("用户修改密码 出参:success=true");
        return Result.success("密码修改成功", null);
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody EncryptedLoginRequest req) {
        log.info("用户登录 入参:username={}", req.getUsername());

        // 1. 校验并消费 nonce
        if (!nonceService.consumeNonce(req.getNonce())) {
            throw new BizException(ResultCode.NONCE_INVALID);
        }

        // 2. 解密加密数据
        String decrypted = rsaKeyService.decrypt(req.getEncryptedData());
        String[] parts = decrypted.split("\\|");

        // 3. 校验解密后的 nonce（从右往左取最后一段，避免密码含竖线导致错位）
        String decryptNonce = parts[parts.length - 1];
        if (!req.getNonce().equals(decryptNonce)) {
            throw new BizException(ResultCode.NONCE_INVALID);
        }

        // 4. 提取密码：password|nonce
        String password = parts[0];

        // 5. 构造旧 DTO 调用 service
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername(req.getUsername());
        loginReq.setPassword(password);
        LoginVO loginVO = authService.login(loginReq);

        log.info("用户登录 出参:userId={}", loginVO.getUserInfo().getUserId());
        return Result.success("登录成功", loginVO);
    }

    @PostMapping("/refresh")
    public Result<LoginVO> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        log.info("刷新Token 入参:refreshToken={}", req.getRefreshToken());
        LoginVO loginVO = authService.refresh(req);
        log.info("刷新Token 出参:userId={}", loginVO.getUserInfo().getUserId());
        return Result.success("Token 刷新成功", loginVO);
    }

    @PostMapping("/logout")
    public Result<?> logout(@RequestHeader("Authorization") String authHeader) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(authHeader);
        log.info("用户登出 入参:userId={}", userId);
        String token = jwtTokenProvider.extractTokenFromHeader(authHeader);
        authService.logout(userId, token);
        log.info("用户登出 出参:success=true");
        return Result.success("登出成功", null);
    }

    @GetMapping("/me")
    public Result<UserVO> me(@RequestHeader("Authorization") String authHeader) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(authHeader);
        log.info("获取当前用户 入参:userId={}", userId);
        UserVO userVO = authService.getCurrentUser(userId);
        log.info("获取当前用户 出参:username={}", userVO.getUsername());
        return Result.success(userVO);
    }
}
