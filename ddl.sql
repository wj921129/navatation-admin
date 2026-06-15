-- MySQL dump
SET NAMES utf8;
SET TIME_ZONE='+00:00';
SET UNIQUE_CHECKS=0;
SET FOREIGN_KEY_CHECKS=0;
SET SQL_MODE='NO_AUTO_VALUE_ON_ZERO';
SET SQL_NOTES=0;

-- 1. 普通用户表
DROP TABLE IF EXISTS `navatation_user`;
CREATE TABLE `navatation_user` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID',
  `username` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `role` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'USER' COMMENT '角色',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用, 1-正常',
  `last_login_at` datetime DEFAULT NULL,
  `last_login_ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `navatation_user_config`;
CREATE TABLE `navatation_user_config` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `config_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `search_engine` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'google',
  `background_image` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `background_type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'URL',
  `search_box_width` int(11) NOT NULL DEFAULT '50',
  `search_box_height` int(11) NOT NULL DEFAULT '64',
  `search_box_margin_top` int(11) NOT NULL DEFAULT '192',
  `icon_size` int(11) NOT NULL DEFAULT '64',
  `icon_radius` int(11) NOT NULL DEFAULT '50',
  `icon_spacing_x` int(11) NOT NULL DEFAULT '32',
  `icon_spacing_y` int(11) NOT NULL DEFAULT '48',
  `icon_text_gap` int(11) NOT NULL DEFAULT '12',
  `text_size` int(11) NOT NULL DEFAULT '14',
  `icons_margin_top` int(11) NOT NULL DEFAULT '64',
  `icons_margin_x` int(11) NOT NULL DEFAULT '10',
  `theme` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'dark',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_config_id` (`config_id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `navatation_nav_category`;
CREATE TABLE `navatation_nav_category` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `category_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` double NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_category_id` (`category_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_sort` (`user_id`,`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `navatation_nav_shortcut`;
CREATE TABLE `navatation_nav_shortcut` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `shortcut_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `url` varchar(2048) COLLATE utf8mb4_unicode_ci NOT NULL,
  `icon_type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'BUILTIN',
  `icon_value` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `icon_color` varchar(7) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sort_order` double NOT NULL DEFAULT '0',
  `click_count` bigint(20) unsigned NOT NULL DEFAULT '0',
  `last_click_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_shortcut_id` (`shortcut_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_category_sort` (`user_id`,`category_id`,`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `navatation_todo_item`;
CREATE TABLE `navatation_todo_item` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `todo_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL,
  `completed` tinyint(1) NOT NULL DEFAULT '0',
  `sort_order` double NOT NULL DEFAULT '0',
  `completed_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_todo_id` (`todo_id`),
  KEY `idx_user_completed` (`user_id`,`completed`),
  KEY `idx_user_sort` (`user_id`,`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `navatation_user_widget`;
CREATE TABLE `navatation_user_widget` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `widget_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `style` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `x` decimal(5,2) NOT NULL,
  `y` decimal(5,2) NOT NULL,
  `meta` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_widget_id` (`widget_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Recommend 推荐表
DROP TABLE IF EXISTS `navatation_recommend_config`;
CREATE TABLE `navatation_recommend_config` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `config_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `search_engine` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'google',
  `background_image` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `background_type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'URL',
  `search_box_width` int(11) NOT NULL DEFAULT '50',
  `search_box_height` int(11) NOT NULL DEFAULT '64',
  `search_box_margin_top` int(11) NOT NULL DEFAULT '192',
  `icon_size` int(11) NOT NULL DEFAULT '64',
  `icon_radius` int(11) NOT NULL DEFAULT '50',
  `icon_spacing_x` int(11) NOT NULL DEFAULT '32',
  `icon_spacing_y` int(11) NOT NULL DEFAULT '48',
  `icon_text_gap` int(11) NOT NULL DEFAULT '12',
  `text_size` int(11) NOT NULL DEFAULT '14',
  `icons_margin_top` int(11) NOT NULL DEFAULT '64',
  `icons_margin_x` int(11) NOT NULL DEFAULT '10',
  `theme` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'dark',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_config_id` (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `navatation_recommend_category`;
CREATE TABLE `navatation_recommend_category` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `category_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` double NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_category_id` (`category_id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `navatation_recommend_shortcut`;
CREATE TABLE `navatation_recommend_shortcut` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `shortcut_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `url` varchar(2048) COLLATE utf8mb4_unicode_ci NOT NULL,
  `icon_type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'BUILTIN',
  `icon_value` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `icon_color` varchar(7) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sort_order` double NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_shortcut_id` (`shortcut_id`),
  KEY `idx_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `navatation_recommend_todo_item`;
CREATE TABLE `navatation_recommend_todo_item` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `todo_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL,
  `completed` tinyint(1) NOT NULL DEFAULT '0',
  `sort_order` double NOT NULL DEFAULT '0',
  `completed_at` datetime DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_todo_id` (`todo_id`),
  KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `navatation_recommend_widget`;
CREATE TABLE `navatation_recommend_widget` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `widget_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `style` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `x` decimal(5,2) NOT NULL,
  `y` decimal(5,2) NOT NULL,
  `meta` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_widget_id` (`widget_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 移除旧的历史表（防备数据库里还有残留）
DROP TABLE IF EXISTS `navatation_recommend_site`;
DROP TABLE IF EXISTS `navatation_root_config`;
DROP TABLE IF EXISTS `navatation_root_nav_category`;
DROP TABLE IF EXISTS `navatation_root_nav_shortcut`;
DROP TABLE IF EXISTS `navatation_root_todo_item`;
DROP TABLE IF EXISTS `navatation_root_user`;
DROP TABLE IF EXISTS `navatation_root_user_widget`;

SET FOREIGN_KEY_CHECKS=1;
SET UNIQUE_CHECKS=1;
