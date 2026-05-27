package com.navatation.common;

/**
 * @Author PM
 * @Description Redis 统一 Key 前缀及缓存配置常量类
 * 集中管理项目中所有的 Redis Key 前缀，防止命名冲突，便于维护与治理。
 */
public class RedisConstants {

    /**
     * ==========================================
     * 1. 用户认证与安全模块 (Auth & Security)
     * ==========================================
     */

    /** Nonce 一次性挑战码前缀 */
    public static final String KEY_AUTH_NONCE = "auth:nonce:";

    /** 用户 Refresh Token 前缀 (关联 userId) */
    public static final String KEY_AUTH_REFRESH_TOKEN = "auth:refresh_token:";

    /** JWT Token 黑名单前缀 (关联 tokenId) */
    public static final String KEY_AUTH_BLACKLIST = "auth:blacklist:";

    /**
     * ==========================================
     * 2. 导航模块 (Navigation & Favicon)
     * ==========================================
     */

    /** 网站 Favicon 抓取缓存前缀 (关联 host) */
    public static final String KEY_NAV_FAVICON = "nav:favicon:";

    /** 自定义图标文件上传速率限制前缀 (关联 userId) */
    public static final String KEY_NAV_RATE_UPLOAD = "nav:rate:icon_upload:";

}
