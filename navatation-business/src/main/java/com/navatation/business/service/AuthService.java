package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.navatation.business.dto.LoginRequest;
import com.navatation.business.dto.LoginVO;
import com.navatation.business.dto.RefreshTokenRequest;
import com.navatation.business.dto.ChangePasswordRequest;
import com.navatation.business.dto.RegisterRequest;
import com.navatation.business.dto.UserVO;
import com.navatation.business.entity.User;
import com.navatation.business.entity.UserConfig;
import com.navatation.business.mapper.NavCategoryMapper;
import com.navatation.business.mapper.UserConfigMapper;
import com.navatation.business.mapper.UserMapper;
import com.navatation.common.BizException;
import com.navatation.common.ResultCode;
import com.navatation.framework.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description 认证服务，处理用户注册、登录、登出、Token刷新等核心认证逻辑 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final int USER_STATUS_DISABLED = 0;
    private static final int USER_STATUS_ENABLED = 1;

    private final UserMapper userMapper;
    private final UserConfigMapper userConfigMapper;
    private final NavCategoryMapper navCategoryMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户登录，校验用户名密码并生成Token对
     * @param req 登录请求
     * @return 登录响应（含Token和用户信息） */
    @Transactional
    public LoginVO login(LoginRequest req) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (user == null || user.getStatus() == USER_STATUS_DISABLED) {
            throw new BizException(ResultCode.PASSWORD_ERROR);
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BizException(ResultCode.PASSWORD_ERROR);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

        // 存储刷新Token到Redis
        redisTemplate.opsForValue().set(
                "refresh_token:" + user.getUserId(),
                refreshToken,
                7, TimeUnit.DAYS);

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);

        UserVO userVO = new UserVO();
        userVO.setUserId(user.getUserId());
        userVO.setUsername(user.getUsername());
        userVO.setAvatar(user.getAvatar());
        userVO.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);

        LoginVO vo = new LoginVO();
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);
        vo.setExpiresIn(jwtTokenProvider.getAccessTokenExpire());
        vo.setUserInfo(userVO);
        log.info("用户登录成功 userId={} username={}", user.getUserId(), user.getUsername());
        return vo;
    }

    /**
     * 用户注册，创建用户、默认配置和默认分类
     * @param req 注册请求 */
    @Transactional
    public void register(RegisterRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new BizException(ResultCode.BAD_REQUEST);
        }
        User exist = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (exist != null) {
            throw new BizException(ResultCode.USERNAME_EXISTS);
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setStatus(USER_STATUS_ENABLED);
        userMapper.insert(user);

        // 创建用户默认配置
        UserConfig config = new UserConfig();
        config.setUserId(user.getUserId());
        userConfigMapper.insert(config);

        // 创建用户默认分类
        com.navatation.business.entity.NavCategory defaultCategory =
                new com.navatation.business.entity.NavCategory();
        defaultCategory.setUserId(user.getUserId());
        defaultCategory.setName("常用");
        defaultCategory.setSortOrder(0);
        navCategoryMapper.insert(defaultCategory);
        log.info("用户注册成功 userId={} username={}", user.getUserId(), user.getUsername());
    }

    /**
     * 修改密码
     * @param userId 当前用户ID
     * @param req 修改密码请求 */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest req) {
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "两次输入新密码不一致");
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userMapper.updateById(user);
        log.info("用户密码修改成功 userId={} username={}", user.getUserId(), user.getUsername());
    }

    /**
     * 刷新Token，验证RefreshToken后颁发新Token对
     * @param req 刷新Token请求
     * @return 新的登录响应 */
    public LoginVO refresh(RefreshTokenRequest req) {
        if (!jwtTokenProvider.validateToken(req.getRefreshToken())) {
            throw new BizException(ResultCode.TOKEN_INVALID);
        }
        Long userId = jwtTokenProvider.getUserIdFromToken(req.getRefreshToken());
        String storedToken = (String) redisTemplate.opsForValue().get("refresh_token:" + userId);

        if (storedToken == null || !storedToken.equals(req.getRefreshToken())) {
            throw new BizException(ResultCode.TOKEN_INVALID);
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, user.getUsername());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);

        redisTemplate.opsForValue().set(
                "refresh_token:" + userId, newRefreshToken, 7, TimeUnit.DAYS);

        UserVO userVO = new UserVO();
        userVO.setUserId(user.getUserId());
        userVO.setUsername(user.getUsername());
        userVO.setAvatar(user.getAvatar());
        userVO.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);

        LoginVO vo = new LoginVO();
        vo.setAccessToken(newAccessToken);
        vo.setRefreshToken(newRefreshToken);
        vo.setUserInfo(userVO);
        vo.setExpiresIn(jwtTokenProvider.getAccessTokenExpire());
        log.info("Token刷新成功 userId={}", userId);
        return vo;
    }

    /**
     * 用户登出，将AccessToken加入黑名单并移除RefreshToken
     * @param userId 用户ID
     * @param token 待作废的AccessToken */
    public void logout(Long userId, String token) {
        String tokenId;
        try {
            tokenId = jwtTokenProvider.parseToken(token).getId();
        } catch (Exception e) {
            log.error("解析登出Token失败", e);
            return;
        }
        // 将访问Token加入黑名单
        long remaining = jwtTokenProvider.getAccessTokenExpire();
        redisTemplate.opsForValue().set("blacklist:" + tokenId, "1", remaining, TimeUnit.SECONDS);
        // 移除刷新Token
        redisTemplate.delete("refresh_token:" + userId);
        log.info("用户登出成功 userId={}", userId);
    }

    /**
     * 获取当前登录用户信息
     * @param userId 用户ID
     * @return 用户信息 */
    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }
        UserVO vo = new UserVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setAvatar(user.getAvatar());
        vo.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        return vo;
    }
}
