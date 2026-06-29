-- Navatation DDL
-- ------------------------------------------------------

DROP TABLE IF EXISTS `navatation_nav_category`;
CREATE TABLE `navatation_nav_category` (
  `row_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `category_id` varchar(64) NOT NULL,
  `name` varchar(32) NOT NULL,
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `sort_order` decimal(10,2) NOT NULL DEFAULT 0.00,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常, 1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_category_id` (`category_id`),
  KEY `idx_user_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `navatation_nav_home_shortcut`;
CREATE TABLE `navatation_nav_home_shortcut` (
  `row_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `shortcut_id` varchar(64) NOT NULL,
  `category_id` varchar(64) DEFAULT NULL COMMENT '分类ID，NULL表示不属于任何分类',
  `user_id` varchar(64) NOT NULL,
  `type` varchar(8) NOT NULL DEFAULT 'single' COMMENT '类型：single-普通图标, stack-堆叠组',
  `stack_id` varchar(64) DEFAULT NULL COMMENT '所属堆叠组ID，NULL表示顶层项',
  `stack_name` varchar(64) DEFAULT NULL COMMENT '堆叠组名称（仅type=stack时有值）',
  `name` varchar(64) NOT NULL,
  `url` varchar(2048) NOT NULL DEFAULT '',
  `icon_type` varchar(16) NOT NULL DEFAULT 'BUILTIN',
  `icon_value` varchar(2048) DEFAULT NULL,
  `icon_color` varchar(7) DEFAULT NULL,
  `sort_order` decimal(10,2) NOT NULL DEFAULT 0.00,
  `click_count` bigint unsigned NOT NULL DEFAULT 0,
  `last_click_at` datetime DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常, 1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_shortcut_id` (`shortcut_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_stack_id` (`stack_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `navatation_recommend_config`;
CREATE TABLE `navatation_recommend_config` (
  `row_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `config_id` varchar(64) NOT NULL,
  `search_engine` varchar(16) NOT NULL DEFAULT 'google',
  `background_image` varchar(2048) DEFAULT NULL,
  `background_type` varchar(16) NOT NULL DEFAULT 'URL',
  `search_box_width` int NOT NULL DEFAULT 50,
  `search_box_height` int NOT NULL DEFAULT 64,
  `search_box_margin_top` int NOT NULL DEFAULT 192,
  `icon_size` int NOT NULL DEFAULT 64,
  `icon_radius` int NOT NULL DEFAULT 50,
  `icon_spacing_x` int NOT NULL DEFAULT 32,
  `icon_spacing_y` int NOT NULL DEFAULT 48,
  `icon_text_gap` int NOT NULL DEFAULT 12,
  `text_size` int NOT NULL DEFAULT 14,
  `icons_margin_top` int NOT NULL DEFAULT 64,
  `icons_margin_x` int NOT NULL DEFAULT 10,
  `theme` varchar(16) NOT NULL DEFAULT 'dark',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常, 1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_config_id` (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `navatation_recommend_home_shortcut`;
CREATE TABLE `navatation_recommend_home_shortcut` (
  `row_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `shortcut_id` varchar(64) NOT NULL,
  `type` varchar(8) NOT NULL DEFAULT 'single' COMMENT '类型：single-普通图标, stack-堆叠组',
  `stack_id` varchar(64) DEFAULT NULL COMMENT '所属堆叠组ID，NULL表示顶层项',
  `stack_name` varchar(64) DEFAULT NULL COMMENT '堆叠组名称（仅type=stack时有值）',
  `name` varchar(64) NOT NULL,
  `url` varchar(2048) NOT NULL DEFAULT '',
  `icon_type` varchar(16) NOT NULL DEFAULT 'BUILTIN',
  `icon_value` varchar(2048) DEFAULT NULL,
  `icon_color` varchar(7) DEFAULT NULL,
  `sort_order` decimal(10,2) NOT NULL DEFAULT 0.00,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_shortcut_id` (`shortcut_id`),
  KEY `idx_stack_id` (`stack_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `navatation_recommend_shortcut`;
CREATE TABLE `navatation_recommend_shortcut` (
  `row_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `shortcut_id` varchar(64) NOT NULL,
  `category_id` varchar(64) NOT NULL,
  `name` varchar(64) NOT NULL,
  `url` varchar(2048) NOT NULL DEFAULT '',
  `icon_type` varchar(16) NOT NULL DEFAULT 'BUILTIN',
  `icon_value` varchar(2048) DEFAULT NULL,
  `icon_color` varchar(7) DEFAULT NULL,
  `sort_order` decimal(10,2) NOT NULL DEFAULT 0.00,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常, 1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_shortcut_id` (`shortcut_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `navatation_recommend_todo_item`;
CREATE TABLE `navatation_recommend_todo_item` (
  `row_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `todo_id` varchar(64) NOT NULL,
  `content` varchar(512) NOT NULL,
  `sort_order` decimal(10,2) NOT NULL DEFAULT 0.00,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常, 1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_todo_id` (`todo_id`),
  KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `navatation_recommend_widget`;
CREATE TABLE `navatation_recommend_widget` (
  `row_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `widget_id` varchar(64) NOT NULL,
  `type` varchar(32) NOT NULL,
  `style` varchar(32) NOT NULL,
  `x` decimal(5,2) NOT NULL,
  `y` decimal(5,2) NOT NULL,
  `meta` json DEFAULT NULL COMMENT '组件配置JSON',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常, 1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_widget_id` (`widget_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `navatation_todo_item`;
CREATE TABLE `navatation_todo_item` (
  `row_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `todo_id` varchar(64) NOT NULL,
  `user_id` varchar(64) NOT NULL,
  `content` varchar(512) NOT NULL,
  `completed` tinyint NOT NULL DEFAULT 0,
  `sort_order` decimal(10,2) NOT NULL DEFAULT 0.00,
  `completed_at` datetime DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常, 1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_todo_id` (`todo_id`),
  KEY `idx_user_completed` (`user_id`,`completed`),
  KEY `idx_user_sort` (`user_id`,`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `navatation_user`;
CREATE TABLE `navatation_user` (
  `row_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `username` varchar(20) NOT NULL COMMENT '用户名',
  `password` varchar(128) NOT NULL COMMENT '密码',
  `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(512) DEFAULT NULL COMMENT '头像URL',
  `role` varchar(16) NOT NULL DEFAULT 'USER' COMMENT '角色',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用, 1-正常',
  `last_login_at` datetime DEFAULT NULL,
  `last_login_ip` varchar(45) DEFAULT NULL,
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常, 1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `navatation_user_config`;
CREATE TABLE `navatation_user_config` (
  `row_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `config_id` varchar(64) NOT NULL,
  `user_id` varchar(64) NOT NULL,
  `search_engine` varchar(16) NOT NULL DEFAULT 'google',
  `background_image` varchar(2048) DEFAULT NULL,
  `background_type` varchar(16) NOT NULL DEFAULT 'URL',
  `search_box_width` int NOT NULL DEFAULT 50,
  `search_box_height` int NOT NULL DEFAULT 64,
  `search_box_margin_top` int NOT NULL DEFAULT 192,
  `icon_size` int NOT NULL DEFAULT 64,
  `icon_radius` int NOT NULL DEFAULT 50,
  `icon_spacing_x` int NOT NULL DEFAULT 32,
  `icon_spacing_y` int NOT NULL DEFAULT 48,
  `icon_text_gap` int NOT NULL DEFAULT 12,
  `text_size` int NOT NULL DEFAULT 14,
  `icons_margin_top` int NOT NULL DEFAULT 64,
  `icons_margin_x` int NOT NULL DEFAULT 10,
  `theme` varchar(16) NOT NULL DEFAULT 'dark',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常, 1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_config_id` (`config_id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `navatation_user_widget`;
CREATE TABLE `navatation_user_widget` (
  `row_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `widget_id` varchar(64) NOT NULL,
  `user_id` varchar(64) NOT NULL,
  `type` varchar(32) NOT NULL,
  `style` varchar(32) NOT NULL,
  `x` decimal(5,2) NOT NULL,
  `y` decimal(5,2) NOT NULL,
  `meta` json DEFAULT NULL COMMENT '组件配置JSON',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常, 1-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_widget_id` (`widget_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
