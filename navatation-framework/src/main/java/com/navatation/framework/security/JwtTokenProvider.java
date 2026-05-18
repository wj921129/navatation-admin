package com.navatation.framework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description JWT Token 工具类，负责AccessToken和RefreshToken的生成、解析与校验 */
@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final SecretKey secretKey;
    private final long accessTokenExpire;
    private final long refreshTokenExpire;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expire:7200}") long accessTokenExpire,
            @Value("${app.jwt.refresh-token-expire:604800}") long refreshTokenExpire) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpire = accessTokenExpire;
        this.refreshTokenExpire = refreshTokenExpire;
    }

    /**
     * 生成访问令牌
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT AccessToken */
    public String generateAccessToken(Long userId, String username) {
        Date now = new Date();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + accessTokenExpire * 1000))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 生成刷新令牌
     * @param userId 用户ID
     * @return JWT RefreshToken */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshTokenExpire * 1000))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 解析Token载荷
     * @param token JWT字符串
     * @return Claims载荷 */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 校验Token是否有效
     * @param token JWT字符串
     * @return true有效/false无效 */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Token校验失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从Token中提取用户ID
     * @param token JWT字符串
     * @return 用户ID */
    public Long getUserIdFromToken(String token) {
        return parseToken(token).get("userId", Long.class);
    }

    /**
     * 获取AccessToken过期时间（秒）
     * @return 过期秒数 */
    public long getAccessTokenExpire() {
        return accessTokenExpire;
    }

    /**
     * 从 Authorization 请求头中提取 Token 字符串（去除 Bearer 前缀）
     * @param authHeader Authorization 请求头
     * @return Token 字符串 */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("无效的 Authorization 请求头");
    }

    /**
     * 从 Authorization 请求头中直接获取用户ID
     * @param authHeader Authorization 请求头
     * @return 用户ID */
    public Long getUserIdFromAuthHeader(String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        return getUserIdFromToken(token);
    }
}
