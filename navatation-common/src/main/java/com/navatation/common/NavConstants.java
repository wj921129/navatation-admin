package com.navatation.common;

import java.util.Set;

/**
 * 导航模块相关常量定义
 */
public class NavConstants {

    /**
     * 内置图标类型
     */
    public static final String ICON_TYPE_BUILTIN = "BUILTIN";

    /**
     * Favicon 图标类型
     */
    public static final String ICON_TYPE_FAVICON = "FAVICON";

    /**
     * 自定义上传图标类型
     */
    public static final String ICON_TYPE_CUSTOM = "CUSTOM_UPLOAD";

    /**
     * 默认分类名称
     */
    public static final String DEFAULT_CATEGORY_NAME = "常用";

    /**
     * 允许上传的图标 MIME 类型白名单
     */
    public static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/png", "image/jpeg", "image/gif", "image/webp",
            "image/x-icon", "image/vnd.microsoft.icon", "image/svg+xml"
    );

    /**
     * 图标文件大小上限：200KB
     */
    public static final long MAX_ICON_SIZE = 200 * 1024;

    /**
     * 每小时每用户最大上传次数
     */
    public static final int MAX_UPLOADS_PER_HOUR = 30;

    /**
     * 批量抓取 Favicon 单次最大上限
     */
    public static final int MAX_BATCH_FAVICON_SIZE = 100;
}
