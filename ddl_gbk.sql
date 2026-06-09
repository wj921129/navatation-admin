-- ============================================================
-- 极简网页浏览器新标签�?(Navatation) �?数据库初始化 DDL
-- ============================================================
DROP DATABASE IF EXISTS `navatation`;
CREATE DATABASE `navatation` DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;
USE `navatation`;

CREATE TABLE IF NOT EXISTS `navatation_user` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '用户ID，业务逻辑主键，带前缀U的随机UUID',
    `username`       VARCHAR(20)           NOT NULL                COMMENT '用户名，3-20字符，唯一',
    `password`       VARCHAR(128)          NOT NULL                COMMENT '密码，BCrypt 加密存储',
    `email`          VARCHAR(128)          DEFAULT NULL            COMMENT '邮箱，用于密码找�?,
    `avatar`         VARCHAR(512)          DEFAULT NULL            COMMENT '头像URL，OSS地址',
    `status`         TINYINT               NOT NULL DEFAULT 1      COMMENT '账号状态：0-禁用, 1-正常',
    `role`           VARCHAR(16)           NOT NULL DEFAULT 'USER' COMMENT '角色：USER-普通用�? ADMIN-超级管理�?,
    `last_login_at`  DATETIME              DEFAULT NULL            COMMENT '最后登录时�?,
    `last_login_ip`  VARCHAR(45)           DEFAULT NULL            COMMENT '最后登录IP',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_nav_category` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '分类ID，业务逻辑主键，带前缀CG的随机UUID',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关�?navatation_user.user_id',
    `name`           VARCHAR(32)           NOT NULL                COMMENT '分类名称，如：常用、工作、娱�?,
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '排序序号，越小越靠前',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_category_id` (`category_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_sort` (`user_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_nav_shortcut` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `shortcut_id`    VARCHAR(64)           NOT NULL                COMMENT '快捷方式ID，业务逻辑主键，带前缀SC的随机UUID',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '所属分类ID，关�?navatation_nav_category.category_id',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关�?navatation_user.user_id（冗余字段，加速查询）',
    `name`           VARCHAR(64)           NOT NULL                COMMENT '网站名称，显示在图标下方',
    `url`            VARCHAR(2048)         NOT NULL                COMMENT '网站URL，点击跳转目�?,
    `icon_type`      VARCHAR(16)           NOT NULL DEFAULT 'BUILTIN' COMMENT '图标类型',
    `icon_value`     VARCHAR(2048)         DEFAULT NULL            COMMENT '图标�?,
    `icon_color`     VARCHAR(7)            DEFAULT NULL            COMMENT '图标颜色',
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '排序序号',
    `click_count`    BIGINT       UNSIGNED NOT NULL DEFAULT 0      COMMENT '点击次数',
    `last_click_at`  DATETIME              DEFAULT NULL            COMMENT '最后点击时�?,
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_shortcut_id` (`shortcut_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_category_sort` (`user_id`, `category_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_user_config` (
    `row_id`                  BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `config_id`               VARCHAR(64)           NOT NULL                COMMENT '配置ID，业务逻辑主键，带前缀UC的随机UUID',
    `user_id`                 VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关�?navatation_user.user_id，唯一',
    `search_engine`           VARCHAR(16)           NOT NULL DEFAULT 'google' COMMENT '默认搜索引擎',
    `background_image`        VARCHAR(2048)         DEFAULT NULL            COMMENT '壁纸URL',
    `background_type`         VARCHAR(16)           NOT NULL DEFAULT 'URL'  COMMENT '壁纸类型',
    `search_box_width`        INT                   NOT NULL DEFAULT 50     COMMENT '搜索框宽度屏幕占�?%)',
    `search_box_height`       INT                   NOT NULL DEFAULT 64     COMMENT '搜索框高度像�?,
    `search_box_margin_top`   INT                   NOT NULL DEFAULT 192    COMMENT '搜索框距顶部距离像素',
    `icon_size`               INT                   NOT NULL DEFAULT 64     COMMENT '图标大小像素',
    `icon_radius`             INT                   NOT NULL DEFAULT 50     COMMENT '图标圆角百分�?,
    `icon_spacing_x`          INT                   NOT NULL DEFAULT 32     COMMENT '图标水平间距像素',
    `icon_spacing_y`          INT                   NOT NULL DEFAULT 48     COMMENT '图标垂直间距像素',
    `icon_text_gap`           INT                   NOT NULL DEFAULT 12     COMMENT '图标与文字间距像�?,
    `text_size`               INT                   NOT NULL DEFAULT 14     COMMENT '文字大小像素',
    `icons_margin_top`        INT                   NOT NULL DEFAULT 64     COMMENT '导航区距搜索框距离像�?,
    `icons_margin_x`          INT                   NOT NULL DEFAULT 10     COMMENT '导航区左右边距百分比',
    `theme`                   VARCHAR(16)           NOT NULL DEFAULT 'dark' COMMENT '主题模式',
    `created_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_config_id` (`config_id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_todo_item` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `todo_id`        VARCHAR(64)           NOT NULL                COMMENT '待办ID，业务逻辑主键，带前缀TD的随机UUID',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关�?navatation_user.user_id',
    `content`        VARCHAR(512)          NOT NULL                COMMENT '待办内容',
    `completed`      TINYINT(1)            NOT NULL DEFAULT 0      COMMENT '完成状�?,
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '排序序号',
    `completed_at`   DATETIME              DEFAULT NULL            COMMENT '完成时间',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_todo_id` (`todo_id`),
    KEY `idx_user_completed` (`user_id`, `completed`),
    KEY `idx_user_sort` (`user_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_recommend_category` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '推荐分类ID，业务逻辑主键，带前缀RC的随机UUID',
    `name`           VARCHAR(32)           NOT NULL                COMMENT '分类名称',
    `icon_name`      VARCHAR(32)           NOT NULL                COMMENT '分类图标�?,
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '排序序号',
    `status`         TINYINT               NOT NULL DEFAULT 1      COMMENT '状态：0-禁用, 1-启用',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_category_id` (`category_id`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_recommend_site` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `site_id`        VARCHAR(64)           NOT NULL                COMMENT '推荐网站ID，业务逻辑主键，带前缀RS的随机UUID',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '所属推荐分类ID，关�?navatation_recommend_category.category_id',
    `name`           VARCHAR(64)           NOT NULL                COMMENT '网站名称',
    `url`            VARCHAR(2048)         NOT NULL                COMMENT '网站URL',
    `icon_type`      VARCHAR(16)           NOT NULL DEFAULT 'BUILTIN' COMMENT '图标类型',
    `icon_value`     VARCHAR(2048)         DEFAULT NULL            COMMENT '图标�?,
    `icon_color`     VARCHAR(7)            DEFAULT NULL            COMMENT '图标颜色',
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '排序序号',
    `status`         TINYINT               NOT NULL DEFAULT 1      COMMENT '状态：0-禁用, 1-启用',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_site_id` (`site_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_category_sort` (`category_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_recommend_config` (
    `row_id`                  BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `config_id`               VARCHAR(64)           NOT NULL                COMMENT '配置ID，业务逻辑主键，带前缀RCG的随机UUID',
    `search_engine`           VARCHAR(16)           NOT NULL DEFAULT 'google' COMMENT '默认搜索引擎',
    `background_image`        VARCHAR(2048)         DEFAULT NULL            COMMENT '壁纸URL',
    `background_type`         VARCHAR(16)           NOT NULL DEFAULT 'URL'  COMMENT '壁纸类型',
    `search_box_width`        INT                   NOT NULL DEFAULT 50     COMMENT '搜索框宽度屏幕占�?%)',
    `search_box_height`       INT                   NOT NULL DEFAULT 64     COMMENT '搜索框高度像�?,
    `search_box_margin_top`   INT                   NOT NULL DEFAULT 192    COMMENT '搜索框距顶部距离像素',
    `icon_size`               INT                   NOT NULL DEFAULT 64     COMMENT '图标大小像素',
    `icon_radius`             INT                   NOT NULL DEFAULT 50     COMMENT '图标圆角百分�?,
    `icon_spacing_x`          INT                   NOT NULL DEFAULT 32     COMMENT '图标水平间距像素',
    `icon_spacing_y`          INT                   NOT NULL DEFAULT 48     COMMENT '图标垂直间距像素',
    `icon_text_gap`           INT                   NOT NULL DEFAULT 12     COMMENT '图标与文字间距像�?,
    `text_size`               INT                   NOT NULL DEFAULT 14     COMMENT '文字大小像素',
    `icons_margin_top`        INT                   NOT NULL DEFAULT 64     COMMENT '导航区距搜索框距离像�?,
    `icons_margin_x`          INT                   NOT NULL DEFAULT 10     COMMENT '导航区左右边距百分比',
    `theme`                   VARCHAR(16)           NOT NULL DEFAULT 'dark' COMMENT '主题模式',
    `created_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_config_id` (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_user_widget` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `widget_id`      VARCHAR(64)           NOT NULL                COMMENT '组件ID，业务逻辑主键，带前缀WG的随�?2位纯数字字符�?,
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关�?navatation_user.user_id',
    `type`           VARCHAR(32)           NOT NULL                COMMENT '组件类型，如：clock, weather, calendar',
    `style`          VARCHAR(32)           NOT NULL                COMMENT '组件样式，如：analog, digital, flip, traditional',
    `x`              DECIMAL(5, 2)         NOT NULL                COMMENT 'X轴百分比位置 (0.00 - 100.00)',
    `y`              DECIMAL(5, 2)         NOT NULL                COMMENT 'Y轴百分比位置 (0.00 - 100.00)',
    `meta`           VARCHAR(2048)         DEFAULT NULL            COMMENT '可选元数据，JSON字符�?,
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_widget_id` (`widget_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_root_config` (
    `row_id`                  BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `config_id`               VARCHAR(64)           NOT NULL                COMMENT '配置ID，业务逻辑主键，带前缀UC的随机UUID',
    `user_id`                 VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关�?navatation_user.user_id，唯一',
    `search_engine`           VARCHAR(16)           NOT NULL DEFAULT 'google' COMMENT '默认搜索引擎',
    `background_image`        VARCHAR(2048)         DEFAULT NULL            COMMENT '壁纸URL',
    `background_type`         VARCHAR(16)           NOT NULL DEFAULT 'URL'  COMMENT '壁纸类型',
    `search_box_width`        INT                   NOT NULL DEFAULT 50     COMMENT '搜索框宽度屏幕占�?%)',
    `search_box_height`       INT                   NOT NULL DEFAULT 64     COMMENT '搜索框高度像�?,
    `search_box_margin_top`   INT                   NOT NULL DEFAULT 192    COMMENT '搜索框距顶部距离像素',
    `icon_size`               INT                   NOT NULL DEFAULT 64     COMMENT '图标大小像素',
    `icon_radius`             INT                   NOT NULL DEFAULT 50     COMMENT '图标圆角百分�?,
    `icon_spacing_x`          INT                   NOT NULL DEFAULT 32     COMMENT '图标水平间距像素',
    `icon_spacing_y`          INT                   NOT NULL DEFAULT 48     COMMENT '图标垂直间距像素',
    `icon_text_gap`           INT                   NOT NULL DEFAULT 12     COMMENT '图标与文字间距像�?,
    `text_size`               INT                   NOT NULL DEFAULT 14     COMMENT '文字大小像素',
    `icons_margin_top`        INT                   NOT NULL DEFAULT 64     COMMENT '导航区距搜索框距离像�?,
    `icons_margin_x`          INT                   NOT NULL DEFAULT 10     COMMENT '导航区左右边距百分比',
    `theme`                   VARCHAR(16)           NOT NULL DEFAULT 'dark' COMMENT '主题模式',
    `created_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_config_id` (`config_id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_root_nav_category` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '分类ID，业务逻辑主键，带前缀CG的随机UUID',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关�?navatation_user.user_id',
    `name`           VARCHAR(32)           NOT NULL                COMMENT '分类名称，如：常用、工作、娱�?,
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '排序序号，越小越靠前',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_category_id` (`category_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_sort` (`user_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_root_nav_shortcut` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `shortcut_id`    VARCHAR(64)           NOT NULL                COMMENT '快捷方式ID，业务逻辑主键，带前缀SC的随机UUID',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '所属分类ID，关�?navatation_root_nav_category.category_id',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关�?navatation_user.user_id（冗余字段，加速查询）',
    `name`           VARCHAR(64)           NOT NULL                COMMENT '网站名称，显示在图标下方',
    `url`            VARCHAR(2048)         NOT NULL                COMMENT '网站URL，点击跳转目�?,
    `icon_type`      VARCHAR(16)           NOT NULL DEFAULT 'BUILTIN' COMMENT '图标类型',
    `icon_value`     VARCHAR(2048)         DEFAULT NULL            COMMENT '图标�?,
    `icon_color`     VARCHAR(7)            DEFAULT NULL            COMMENT '图标颜色',
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '排序序号',
    `click_count`    BIGINT       UNSIGNED NOT NULL DEFAULT 0      COMMENT '点击次数',
    `last_click_at`  DATETIME              DEFAULT NULL            COMMENT '最后点击时�?,
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_shortcut_id` (`shortcut_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_category_sort` (`user_id`, `category_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_root_user_widget` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `widget_id`      VARCHAR(64)           NOT NULL                COMMENT '组件ID，业务逻辑主键，带前缀WG的随�?2位纯数字字符�?,
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关�?navatation_user.user_id',
    `type`           VARCHAR(32)           NOT NULL                COMMENT '组件类型，如：clock, weather, calendar',
    `style`          VARCHAR(32)           NOT NULL                COMMENT '组件样式，如：analog, digital, flip, traditional',
    `x`              DECIMAL(5, 2)         NOT NULL                COMMENT 'X轴百分比位置 (0.00 - 100.00)',
    `y`              DECIMAL(5, 2)         NOT NULL                COMMENT 'Y轴百分比位置 (0.00 - 100.00)',
    `meta`           VARCHAR(2048)         DEFAULT NULL            COMMENT '可选元数据，JSON字符�?,
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_widget_id` (`widget_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_type` (`user_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_root_todo_item` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `todo_id`        VARCHAR(64)           NOT NULL                COMMENT '待办ID，业务逻辑主键，带前缀TD的随机UUID',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关�?navatation_user.user_id',
    `content`        VARCHAR(512)          NOT NULL                COMMENT '待办内容',
    `completed`      TINYINT(1)            NOT NULL DEFAULT 0      COMMENT '完成状�?,
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '排序序号',
    `completed_at`   DATETIME              DEFAULT NULL            COMMENT '完成时间',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_todo_id` (`todo_id`),
    KEY `idx_user_completed` (`user_id`, `completed`),
    KEY `idx_user_sort` (`user_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

