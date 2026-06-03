package com.navatation.common;

/**
 * @Author admin
 * @CreateTime 2026-05-25
 * @Description 集中式业务逻辑 ID 生成工具类，生成带前缀的 22 位纯数字随机字符串
 */
public class IdUtils {

    private static final java.security.SecureRandom RANDOM = new java.security.SecureRandom();

    /**
     * 生成指定长度的纯数字随机字符串
     */
    private static String getRandomDigits(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 用户 ID (User ID) — 前缀 'U'
     */
    public static String genUserId() {
        return "U" + getRandomDigits(22);
    }

    /**
     * 用户配置 ID (Config ID) — 前缀 'UC'
     */
    public static String genConfigId() {
        return "UC" + getRandomDigits(22);
    }

    /**
     * 待办事项 ID (Todo ID) — 前缀 'TD'
     */
    public static String genTodoId() {
        return "TD" + getRandomDigits(22);
    }

    /**
     * 自定义导航分类 ID (Category ID) — 前缀 'CG'
     */
    public static String genCategoryId() {
        return "CG" + getRandomDigits(22);
    }

    /**
     * 自定义快捷方式 ID (Shortcut ID) — 前缀 'SC'
     */
    public static String genShortcutId() {
        return "SC" + getRandomDigits(22);
    }

    /**
     * 推荐分类 ID (Recommend Category ID) — 前缀 'RC'
     */
    public static String genRecommendCategoryId() {
        return "RC" + getRandomDigits(22);
    }

    /**
     * 推荐网址 ID (Recommend Site ID) — 前缀 'RS'
     */
    public static String genRecommendSiteId() {
        return "RS" + getRandomDigits(22);
    }

    /**
     * 生成用户组件 ID (Widget ID) — 前缀 'WG'
     *
     * @return 带前缀 WG 的 22 位纯数字随机字符串
     */
    public static String genWidgetId() {
        return "WG" + getRandomDigits(22);
    }
}

