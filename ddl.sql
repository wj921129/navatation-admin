-- ============================================================
-- 极简网页浏览器新标签页 (Navatation) — 数据库初始化 DDL
-- 数据库：MySQL 5.7+
-- 字符集：utf8mb4
-- 版本：v2.0 (UUID + row_id 架构升级)
-- 最后更新：2026-05-25
-- ============================================================

-- 重置并创建数据库
DROP DATABASE IF EXISTS `navatation`;
CREATE DATABASE `navatation`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `navatation`;

-- ============================================================
-- 1. 用户表 (navatation_user)
-- ============================================================
CREATE TABLE IF NOT EXISTS `navatation_user` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '用户ID，业务逻辑主键，带前缀U的随机UUID',
    `username`       VARCHAR(20)           NOT NULL                COMMENT '用户名，3-20字符，唯一',
    `password`       VARCHAR(128)          NOT NULL                COMMENT '密码，BCrypt 加密存储',
    `email`          VARCHAR(128)          DEFAULT NULL            COMMENT '邮箱，用于密码找回',
    `avatar`         VARCHAR(512)          DEFAULT NULL            COMMENT '头像URL，OSS地址',
    `status`         TINYINT               NOT NULL DEFAULT 1      COMMENT '账号状态：0-禁用, 1-正常',
    `last_login_at`  DATETIME              DEFAULT NULL            COMMENT '最后登录时间',
    `last_login_ip`  VARCHAR(45)           DEFAULT NULL            COMMENT '最后登录IP',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='用户表 - 存储注册用户的基本信息';

-- ============================================================
-- 2. 导航分类表 (navatation_nav_category)
-- ============================================================
CREATE TABLE IF NOT EXISTS `navatation_nav_category` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '分类ID，业务逻辑主键，带前缀CG的随机UUID',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关联 navatation_user.user_id',
    `name`           VARCHAR(32)           NOT NULL                COMMENT '分类名称，如：常用、工作、娱乐',
    `sort_order`     INT                   NOT NULL DEFAULT 0      COMMENT '排序序号，越小越靠前',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_category_id` (`category_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_sort` (`user_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='导航分类表 - 用户自定义的快捷方式分组';

-- ============================================================
-- 3. 导航网址表 (navatation_nav_shortcut)
-- ============================================================
CREATE TABLE IF NOT EXISTS `navatation_nav_shortcut` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `shortcut_id`    VARCHAR(64)           NOT NULL                COMMENT '快捷方式ID，业务逻辑主键，带前缀SC的随机UUID',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '所属分类ID，关联 navatation_nav_category.category_id',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关联 navatation_user.user_id（冗余字段，加速查询）',
    `name`           VARCHAR(64)           NOT NULL                COMMENT '网站名称，显示在图标下方',
    `url`            VARCHAR(2048)         NOT NULL                COMMENT '网站URL，点击跳转目标',
    `icon_type`      VARCHAR(16)           NOT NULL DEFAULT 'BUILTIN' COMMENT '图标类型',
    `icon_value`     VARCHAR(2048)         DEFAULT NULL            COMMENT '图标值',
    `icon_color`     VARCHAR(7)            DEFAULT NULL            COMMENT '图标颜色',
    `sort_order`     INT                   NOT NULL DEFAULT 0      COMMENT '排序序号',
    `click_count`    BIGINT       UNSIGNED NOT NULL DEFAULT 0      COMMENT '点击次数',
    `last_click_at`  DATETIME              DEFAULT NULL            COMMENT '最后点击时间',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_shortcut_id` (`shortcut_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_category_sort` (`user_id`, `category_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='导航网址表 - 用户添加的网站快捷方式';

-- ============================================================
-- 4. 用户配置表 (navatation_user_config)
-- ============================================================
CREATE TABLE IF NOT EXISTS `navatation_user_config` (
    `row_id`                  BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `config_id`               VARCHAR(64)           NOT NULL                COMMENT '配置ID，业务逻辑主键，带前缀UC的随机UUID',
    `user_id`                 VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关联 navatation_user.user_id，唯一',
    `search_engine`           VARCHAR(16)           NOT NULL DEFAULT 'google' COMMENT '默认搜索引擎',
    `background_image`        VARCHAR(2048)         DEFAULT NULL            COMMENT '壁纸URL',
    `background_type`         VARCHAR(16)           NOT NULL DEFAULT 'URL'  COMMENT '壁纸类型',
    `search_box_width`        INT                   NOT NULL DEFAULT 50     COMMENT '搜索框宽度屏幕占比(%)',
    `search_box_height`       INT                   NOT NULL DEFAULT 64     COMMENT '搜索框高度像素',
    `search_box_margin_top`   INT                   NOT NULL DEFAULT 192    COMMENT '搜索框距顶部距离像素',
    `icon_size`               INT                   NOT NULL DEFAULT 64     COMMENT '图标大小像素',
    `icon_radius`             INT                   NOT NULL DEFAULT 50     COMMENT '图标圆角百分比',
    `icon_spacing_x`          INT                   NOT NULL DEFAULT 32     COMMENT '图标水平间距像素',
    `icon_spacing_y`          INT                   NOT NULL DEFAULT 48     COMMENT '图标垂直间距像素',
    `icon_text_gap`           INT                   NOT NULL DEFAULT 12     COMMENT '图标与文字间距像素',
    `text_size`               INT                   NOT NULL DEFAULT 14     COMMENT '文字大小像素',
    `icons_margin_top`        INT                   NOT NULL DEFAULT 64     COMMENT '导航区距搜索框距离像素',
    `icons_margin_x`          INT                   NOT NULL DEFAULT 10     COMMENT '导航区左右边距百分比',
    `theme`                   VARCHAR(16)           NOT NULL DEFAULT 'dark' COMMENT '主题模式',
    `created_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_config_id` (`config_id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='用户配置表 - 存储用户的个性化显示设置';

-- ============================================================
-- 5. 待办事项表 (navatation_todo_item)
-- ============================================================
CREATE TABLE IF NOT EXISTS `navatation_todo_item` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `todo_id`        VARCHAR(64)           NOT NULL                COMMENT '待办ID，业务逻辑主键，带前缀TD的随机UUID',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '所属用户ID，关联 navatation_user.user_id',
    `content`        VARCHAR(512)          NOT NULL                COMMENT '待办内容',
    `completed`      TINYINT(1)            NOT NULL DEFAULT 0      COMMENT '完成状态',
    `sort_order`     INT                   NOT NULL DEFAULT 0      COMMENT '排序序号',
    `completed_at`   DATETIME              DEFAULT NULL            COMMENT '完成时间',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_todo_id` (`todo_id`),
    KEY `idx_user_completed` (`user_id`, `completed`),
    KEY `idx_user_sort` (`user_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='待办事项表 - 用户的轻量级待办任务';

-- ============================================================
-- 6. 推荐分类表 (navatation_recommend_category)
-- ============================================================
CREATE TABLE IF NOT EXISTS `navatation_recommend_category` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '推荐分类ID，业务逻辑主键，带前缀RC的随机UUID',
    `name`           VARCHAR(32)           NOT NULL                COMMENT '分类名称',
    `icon_name`      VARCHAR(32)           NOT NULL                COMMENT '分类图标名',
    `sort_order`     INT                   NOT NULL DEFAULT 0      COMMENT '排序序号',
    `status`         TINYINT               NOT NULL DEFAULT 1      COMMENT '状态：0-禁用, 1-启用',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_category_id` (`category_id`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='推荐分类表 - 系统预置的推荐网站分类';

-- ============================================================
-- 7. 推荐网址表 (navatation_recommend_site)
-- ============================================================
CREATE TABLE IF NOT EXISTS `navatation_recommend_site` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '自增物理主键',
    `site_id`        VARCHAR(64)           NOT NULL                COMMENT '推荐网站ID，业务逻辑主键，带前缀RS的随机UUID',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '所属推荐分类ID，关联 navatation_recommend_category.category_id',
    `name`           VARCHAR(64)           NOT NULL                COMMENT '网站名称',
    `url`            VARCHAR(2048)         NOT NULL                COMMENT '网站URL',
    `icon_type`      VARCHAR(16)           NOT NULL DEFAULT 'BUILTIN' COMMENT '图标类型',
    `icon_value`     VARCHAR(2048)         DEFAULT NULL            COMMENT '图标值',
    `icon_color`     VARCHAR(7)            DEFAULT NULL            COMMENT '图标颜色',
    `sort_order`     INT                   NOT NULL DEFAULT 0      COMMENT '排序序号',
    `status`         TINYINT               NOT NULL DEFAULT 1      COMMENT '状态：0-禁用, 1-启用',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_site_id` (`site_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_category_sort` (`category_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='推荐网址表 - 推荐分类下的具体网站';

-- ============================================================
-- 初始化推荐分类数据
-- ============================================================
INSERT INTO `navatation_recommend_category` (`category_id`, `name`, `icon_name`, `sort_order`) VALUES
('RC1', '看视频',   'Video',        1),
('RC2', 'AI工具',   'Cpu',          2),
('RC3', 'Web开发',  'Code',         3),
('RC4', '购物',     'ShoppingBag',  4),
('RC5', '新闻资讯', 'Newspaper',    5),
('RC6', '游戏',     'Gamepad2',     6),
('RC7', '音乐',     'Music',        7),
('RC8', '办公效率', 'Briefcase',    8);

-- ============================================================
-- 初始化推荐网址数据
-- ============================================================
INSERT INTO `navatation_recommend_site` (`site_id`, `category_id`, `name`, `url`, `icon_type`, `icon_value`, `icon_color`, `sort_order`) VALUES
-- 看视频
('RS1', 'RC1', 'YouTube',  'https://youtube.com',  'BUILTIN', 'Video',    '#FF0000', 1),
('RS2', 'RC1', 'Netflix',  'https://netflix.com',  'BUILTIN', 'Video',    '#E50914', 2),
('RS3', 'RC1', 'Bilibili', 'https://bilibili.com', 'BUILTIN', 'Video',    '#00A1D6', 3),
('RS4', 'RC1', 'Twitch',   'https://twitch.tv',    'BUILTIN', 'Video',    '#9146FF', 4),
-- AI工具
('RS5', 'RC2', 'ChatGPT',    'https://chat.openai.com',  'BUILTIN', 'Cpu',    '#10A37F', 1),
('RS6', 'RC2', 'Claude',     'https://claude.ai',         'BUILTIN', 'Cpu',    '#CC9B7A', 2),
('RS7', 'RC2', 'Midjourney', 'https://midjourney.com',    'BUILTIN', 'Camera', '#000000', 3),
('RS8', 'RC2', 'Gemini',     'https://gemini.google.com', 'BUILTIN', 'Cpu',    '#4285F4', 4),
-- Web开发
('RS9', 'RC3', 'GitHub',         'https://github.com',         'BUILTIN', 'Code',     '#181717', 1),
('RS10', 'RC3', 'Stack Overflow', 'https://stackoverflow.com',  'BUILTIN', 'Code',     '#F58025', 2),
('RS11', 'RC3', 'CodePen',        'https://codepen.io',         'BUILTIN', 'Code',     '#000000', 3),
('RS12', 'RC3', 'MDN',            'https://developer.mozilla.org', 'BUILTIN', 'BookOpen', '#000000', 4),
-- 购物
('RS13', 'RC4', 'Amazon', 'https://amazon.com', 'BUILTIN', 'ShoppingBag', '#FF9900', 1),
('RS14', 'RC4', '淘宝',   'https://taobao.com', 'BUILTIN', 'ShoppingBag', '#FF6A00', 2),
('RS15', 'RC4', '京东',   'https://jd.com',     'BUILTIN', 'ShoppingBag', '#E3393C', 3),
('RS16', 'RC4', 'eBay',   'https://ebay.com',   'BUILTIN', 'ShoppingBag', '#E53238', 4),
-- 新闻资讯
('RS17', 'RC5', 'Reddit',       'https://reddit.com',            'BUILTIN', 'Newspaper', '#FF4500', 1),
('RS18', 'RC5', 'Hacker News',  'https://news.ycombinator.com',  'BUILTIN', 'Newspaper', '#FF6600', 2),
('RS19', 'RC5', 'Medium',       'https://medium.com',            'BUILTIN', 'BookOpen',  '#000000', 3),
('RS20', 'RC5', 'BBC',          'https://bbc.com',               'BUILTIN', 'Newspaper', '#000000', 4),
-- 游戏
('RS21', 'RC6', 'Steam',      'https://store.steampowered.com', 'BUILTIN', 'Gamepad2', '#171A21', 1),
('RS22', 'RC6', 'Epic Games', 'https://epicgames.com',           'BUILTIN', 'Gamepad2', '#313131', 2),
('RS23', 'RC6', 'IGN',        'https://ign.com',                 'BUILTIN', 'Gamepad2', '#D8281F', 3),
('RS24', 'RC6', 'GameSpot',   'https://gamespot.com',            'BUILTIN', 'Gamepad2', '#FF0000', 4),
-- 音乐
('RS25', 'RC7', 'Spotify',       'https://spotify.com',       'BUILTIN', 'Music', '#1DB954', 1),
('RS26', 'RC7', 'Apple Music',   'https://music.apple.com',   'BUILTIN', 'Music', '#FA243C', 2),
('RS27', 'RC7', 'SoundCloud',    'https://soundcloud.com',    'BUILTIN', 'Music', '#FF5500', 3),
('RS28', 'RC7', 'YouTube Music', 'https://music.youtube.com', 'BUILTIN', 'Music', '#FF0000', 4),
-- 办公效率
('RS29', 'RC8', 'Notion', 'https://notion.so',  'BUILTIN', 'Briefcase', '#000000', 1),
('RS30', 'RC8', 'Slack',  'https://slack.com',  'BUILTIN', 'Briefcase', '#4A154B', 2),
('RS31', 'RC8', 'Trello', 'https://trello.com', 'BUILTIN', 'Briefcase', '#0052CC', 3),
('RS32', 'RC8', 'Figma',  'https://figma.com',  'BUILTIN', 'Briefcase', '#F24E1E', 4);

-- ============================================================
-- 存量用户数据适配：前端响应式搜索框占比修改
-- ============================================================
UPDATE `navatation_user_config` SET `search_box_width` = ROUND(`search_box_width` * 0.5) WHERE `search_box_width` > 50;
