package com.navatation.business.controller;

import com.navatation.business.dto.LoginRequest;
import com.navatation.business.dto.LoginVO;
import com.navatation.business.dto.RefreshTokenRequest;
import com.navatation.business.dto.RegisterRequest;
import com.navatation.business.dto.UserVO;
import com.navatation.business.service.AuthService;
import com.navatation.common.Result;
import com.navatation.framework.security.JwtTokenProvider;
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

    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest req) {
        log.info("用户注册 入参:username={}", req.getUsername());
        authService.register(req);
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername(req.getUsername());
        loginReq.setPassword(req.getPassword());
        LoginVO loginVO = authService.login(loginReq);
        log.info("用户注册 出参:userId={}", loginVO.getUserInfo().getUserId());
        return Result.success("注册成功", loginVO.getUserInfo());
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest req) {
        log.info("用户登录 入参:username={}", req.getUsername());
        LoginVO loginVO = authService.login(req);
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
        Long userId = jwtTokenProvider.getUserIdFromAuthHeader(authHeader);
        log.info("用户登出 入参:userId={}", userId);
        String token = jwtTokenProvider.extractTokenFromHeader(authHeader);
        authService.logout(userId, token);
        log.info("用户登出 出参:success=true");
        return Result.success("登出成功", null);
    }

    @GetMapping("/me")
    public Result<UserVO> me(@RequestHeader("Authorization") String authHeader) {
        Long userId = jwtTokenProvider.getUserIdFromAuthHeader(authHeader);
        log.info("获取当前用户 入参:userId={}", userId);
        UserVO userVO = authService.getCurrentUser(userId);
        log.info("获取当前用户 出参:username={}", userVO.getUsername());
        return Result.success(userVO);
    }
}
