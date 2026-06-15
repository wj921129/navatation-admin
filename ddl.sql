-- MySQL dump 10.13  Distrib 5.7.29, for Win64 (x86_64)
--
-- Host: localhost    Database: navatation
-- ------------------------------------------------------
-- Server version	5.7.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `navatation_nav_category`
--

DROP TABLE IF EXISTS `navatation_nav_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_nav_category` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `category_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类ID，业务逻辑主键，带前缀CG的随机UUID',
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属用户ID，关联 navatation_user.user_id',
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称，如：常用、工作、娱乐',
  `sort_order` double NOT NULL DEFAULT '0' COMMENT '排序序号，越小越靠前',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_category_id` (`category_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_sort` (`user_id`,`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_nav_shortcut`
--

DROP TABLE IF EXISTS `navatation_nav_shortcut`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_nav_shortcut` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `shortcut_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '快捷方式ID，业务逻辑主键，带前缀SC的随机UUID',
  `category_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属分类ID，关联 navatation_nav_category.category_id',
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属用户ID，关联 navatation_user.user_id（冗余字段，加速查询）',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站名称，显示在图标下方',
  `url` varchar(2048) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站URL，点击跳转目标',
  `icon_type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'BUILTIN' COMMENT '图标类型',
  `icon_value` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标值',
  `icon_color` varchar(7) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标颜色',
  `sort_order` double NOT NULL DEFAULT '0' COMMENT '排序序号',
  `click_count` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '点击次数',
  `last_click_at` datetime DEFAULT NULL COMMENT '最后点击时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_shortcut_id` (`shortcut_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_category_sort` (`user_id`,`category_id`,`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_recommend_category`
--

DROP TABLE IF EXISTS `navatation_recommend_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_recommend_category` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `category_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '推荐分类ID，业务逻辑主键，带前缀RC的随机UUID',
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `icon_name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类图标名',
  `sort_order` double NOT NULL DEFAULT '0' COMMENT '排序序号',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用, 1-启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_category_id` (`category_id`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_recommend_config`
--

DROP TABLE IF EXISTS `navatation_recommend_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_recommend_config` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `config_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置ID，业务逻辑主键，带前缀RCG的随机UUID',
  `search_engine` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'google' COMMENT '默认搜索引擎',
  `background_image` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '壁纸URL',
  `background_type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'URL' COMMENT '壁纸类型',
  `search_box_width` int(11) NOT NULL DEFAULT '50' COMMENT '搜索框宽度屏幕占比(%)',
  `search_box_height` int(11) NOT NULL DEFAULT '64' COMMENT '搜索框高度像素',
  `search_box_margin_top` int(11) NOT NULL DEFAULT '192' COMMENT '搜索框距顶部距离像素',
  `icon_size` int(11) NOT NULL DEFAULT '64' COMMENT '图标大小像素',
  `icon_radius` int(11) NOT NULL DEFAULT '50' COMMENT '图标圆角百分比',
  `icon_spacing_x` int(11) NOT NULL DEFAULT '32' COMMENT '图标水平间距像素',
  `icon_spacing_y` int(11) NOT NULL DEFAULT '48' COMMENT '图标垂直间距像素',
  `icon_text_gap` int(11) NOT NULL DEFAULT '12' COMMENT '图标与文字间距像素',
  `text_size` int(11) NOT NULL DEFAULT '14' COMMENT '文字大小像素',
  `icons_margin_top` int(11) NOT NULL DEFAULT '64' COMMENT '导航区距搜索框距离像素',
  `icons_margin_x` int(11) NOT NULL DEFAULT '10' COMMENT '导航区左右边距百分比',
  `theme` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'dark' COMMENT '主题模式',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_config_id` (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_recommend_site`
--

DROP TABLE IF EXISTS `navatation_recommend_site`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_recommend_site` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `site_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '推荐网站ID，业务逻辑主键，带前缀RS的随机UUID',
  `category_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属推荐分类ID，关联 navatation_recommend_category.category_id',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站名称',
  `url` varchar(2048) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站URL',
  `icon_type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'BUILTIN' COMMENT '图标类型',
  `icon_value` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标值',
  `icon_color` varchar(7) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标颜色',
  `sort_order` double NOT NULL DEFAULT '0' COMMENT '排序序号',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用, 1-启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_site_id` (`site_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_category_sort` (`category_id`,`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_root_config`
--

DROP TABLE IF EXISTS `navatation_root_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_root_config` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `config_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置ID，业务逻辑主键，带前缀UC的随机UUID',
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属用户ID，关联 navatation_user.user_id，唯一',
  `search_engine` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'google' COMMENT '默认搜索引擎',
  `background_image` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '壁纸URL',
  `background_type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'URL' COMMENT '壁纸类型',
  `search_box_width` int(11) NOT NULL DEFAULT '50' COMMENT '搜索框宽度屏幕占比(%)',
  `search_box_height` int(11) NOT NULL DEFAULT '64' COMMENT '搜索框高度像素',
  `search_box_margin_top` int(11) NOT NULL DEFAULT '192' COMMENT '搜索框距顶部距离像素',
  `icon_size` int(11) NOT NULL DEFAULT '64' COMMENT '图标大小像素',
  `icon_radius` int(11) NOT NULL DEFAULT '50' COMMENT '图标圆角百分比',
  `icon_spacing_x` int(11) NOT NULL DEFAULT '32' COMMENT '图标水平间距像素',
  `icon_spacing_y` int(11) NOT NULL DEFAULT '48' COMMENT '图标垂直间距像素',
  `icon_text_gap` int(11) NOT NULL DEFAULT '12' COMMENT '图标与文字间距像素',
  `text_size` int(11) NOT NULL DEFAULT '14' COMMENT '文字大小像素',
  `icons_margin_top` int(11) NOT NULL DEFAULT '64' COMMENT '导航区距搜索框距离像素',
  `icons_margin_x` int(11) NOT NULL DEFAULT '10' COMMENT '导航区左右边距百分比',
  `theme` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'dark' COMMENT '主题模式',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_config_id` (`config_id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_root_nav_category`
--

DROP TABLE IF EXISTS `navatation_root_nav_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_root_nav_category` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `category_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类ID，业务逻辑主键，带前缀CG的随机UUID',
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属用户ID，关联 navatation_user.user_id',
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称，如：常用、工作、娱乐',
  `sort_order` double NOT NULL DEFAULT '0' COMMENT '排序序号，越小越靠前',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_category_id` (`category_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_sort` (`user_id`,`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_root_nav_shortcut`
--

DROP TABLE IF EXISTS `navatation_root_nav_shortcut`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_root_nav_shortcut` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `shortcut_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '快捷方式ID，业务逻辑主键，带前缀SC的随机UUID',
  `category_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属分类ID，关联 navatation_root_nav_category.category_id',
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属用户ID，关联 navatation_user.user_id（冗余字段，加速查询）',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站名称，显示在图标下方',
  `url` varchar(2048) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '网站URL，点击跳转目标',
  `icon_type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'BUILTIN' COMMENT '图标类型',
  `icon_value` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标值',
  `icon_color` varchar(7) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标颜色',
  `sort_order` double NOT NULL DEFAULT '0' COMMENT '排序序号',
  `click_count` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '点击次数',
  `last_click_at` datetime DEFAULT NULL COMMENT '最后点击时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_shortcut_id` (`shortcut_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_category_sort` (`user_id`,`category_id`,`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_root_todo_item`
--

DROP TABLE IF EXISTS `navatation_root_todo_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_root_todo_item` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `todo_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '待办ID，业务逻辑主键，带前缀TD的随机UUID',
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属用户ID，关联 navatation_user.user_id',
  `content` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '待办内容',
  `completed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '完成状态',
  `sort_order` double NOT NULL DEFAULT '0' COMMENT '排序序号',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_todo_id` (`todo_id`),
  KEY `idx_user_completed` (`user_id`,`completed`),
  KEY `idx_user_sort` (`user_id`,`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_root_user`
--

DROP TABLE IF EXISTS `navatation_root_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_root_user` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID，业务逻辑主键，带前缀U的随机UUID',
  `username` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名，3-20字符，唯一',
  `password` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码，BCrypt 加密存储',
  `email` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱，用于密码找回',
  `avatar` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL，OSS地址',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '账号状态：0-禁用, 1-正常',
  `last_login_at` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后登录IP',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_root_user_widget`
--

DROP TABLE IF EXISTS `navatation_root_user_widget`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_root_user_widget` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `widget_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件ID，业务逻辑主键，带前缀WG的随机12位纯数字字符串',
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属用户ID，关联 navatation_user.user_id',
  `type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件类型，如：clock, weather, calendar',
  `style` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件样式，如：analog, digital, flip, traditional',
  `x` decimal(5,2) NOT NULL COMMENT 'X轴百分比位置 (0.00 - 100.00)',
  `y` decimal(5,2) NOT NULL COMMENT 'Y轴百分比位置 (0.00 - 100.00)',
  `meta` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可选元数据，JSON字符串',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_widget_id` (`widget_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_type` (`user_id`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_todo_item`
--

DROP TABLE IF EXISTS `navatation_todo_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_todo_item` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `todo_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '待办ID，业务逻辑主键，带前缀TD的随机UUID',
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属用户ID，关联 navatation_user.user_id',
  `content` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '待办内容',
  `completed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '完成状态',
  `sort_order` double NOT NULL DEFAULT '0' COMMENT '排序序号',
  `completed_at` datetime DEFAULT NULL COMMENT '完成时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_todo_id` (`todo_id`),
  KEY `idx_user_completed` (`user_id`,`completed`),
  KEY `idx_user_sort` (`user_id`,`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_user`
--

DROP TABLE IF EXISTS `navatation_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_user` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户ID，业务逻辑主键，带前缀U的随机UUID',
  `username` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名，3-20字符，唯一',
  `password` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码，BCrypt 加密存储',
  `email` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱，用于密码找回',
  `avatar` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL，OSS地址',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '账号状态：0-禁用, 1-正常',
  `last_login_at` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最后登录IP',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_user_config`
--

DROP TABLE IF EXISTS `navatation_user_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_user_config` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `config_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置ID，业务逻辑主键，带前缀UC的随机UUID',
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属用户ID，关联 navatation_user.user_id，唯一',
  `search_engine` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'google' COMMENT '默认搜索引擎',
  `background_image` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '壁纸URL',
  `background_type` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'URL' COMMENT '壁纸类型',
  `search_box_width` int(11) NOT NULL DEFAULT '50' COMMENT '搜索框宽度屏幕占比(%)',
  `search_box_height` int(11) NOT NULL DEFAULT '64' COMMENT '搜索框高度像素',
  `search_box_margin_top` int(11) NOT NULL DEFAULT '192' COMMENT '搜索框距顶部距离像素',
  `icon_size` int(11) NOT NULL DEFAULT '64' COMMENT '图标大小像素',
  `icon_radius` int(11) NOT NULL DEFAULT '50' COMMENT '图标圆角百分比',
  `icon_spacing_x` int(11) NOT NULL DEFAULT '32' COMMENT '图标水平间距像素',
  `icon_spacing_y` int(11) NOT NULL DEFAULT '48' COMMENT '图标垂直间距像素',
  `icon_text_gap` int(11) NOT NULL DEFAULT '12' COMMENT '图标与文字间距像素',
  `text_size` int(11) NOT NULL DEFAULT '14' COMMENT '文字大小像素',
  `icons_margin_top` int(11) NOT NULL DEFAULT '64' COMMENT '导航区距搜索框距离像素',
  `icons_margin_x` int(11) NOT NULL DEFAULT '10' COMMENT '导航区左右边距百分比',
  `theme` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'dark' COMMENT '主题模式',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_config_id` (`config_id`),
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `navatation_user_widget`
--

DROP TABLE IF EXISTS `navatation_user_widget`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `navatation_user_widget` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增物理主键',
  `widget_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件ID，业务逻辑主键，带前缀WG的随机12位纯数字字符串',
  `user_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '所属用户ID，关联 navatation_user.user_id',
  `type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件类型，如：clock, weather, calendar',
  `style` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组件样式，如：analog, digital, flip, traditional',
  `x` decimal(5,2) NOT NULL COMMENT 'X轴百分比位置 (0.00 - 100.00)',
  `y` decimal(5,2) NOT NULL COMMENT 'Y轴百分比位置 (0.00 - 100.00)',
  `meta` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可选元数据，JSON字符串',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_widget_id` (`widget_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-12 14:22:32


CREATE TABLE IF NOT EXISTS `navatation_recommend_category` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷鐗╃悊涓婚敭',
  `category_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍒嗙被ID',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍒嗙被鍚嶇О',
  `icon_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '鍥炬爣鍚嶇О',
  `sort_order` decimal(10,2) DEFAULT '0.00' COMMENT '鎺掑簭',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_recommend_config` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷鐗╃悊涓婚敭',
  `config_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '閰嶇疆ID',
  `search_engine` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '鎼滅储寮曟搸',
  `background_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '鑳屾櫙鍥剧墖',
  `background_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '鑳屾櫙绫诲瀷',
  `search_box_width` int(11) DEFAULT NULL COMMENT '鎼滅储妗嗗搴?,
  `search_box_height` int(11) DEFAULT NULL COMMENT '鎼滅储妗嗛珮搴?,
  `search_box_margin_top` int(11) DEFAULT NULL COMMENT '鎼滅储妗嗚窛椤?,
  `icon_size` int(11) DEFAULT NULL COMMENT '鍥炬爣澶у皬',
  `icon_radius` int(11) DEFAULT NULL COMMENT '鍥炬爣鍦嗚',
  `icon_spacing_x` int(11) DEFAULT NULL COMMENT '姘村钩闂磋窛',
  `icon_spacing_y` int(11) DEFAULT NULL COMMENT '鍨傜洿闂磋窛',
  `icon_text_gap` int(11) DEFAULT NULL COMMENT '鍥炬枃闂磋窛',
  `text_size` int(11) DEFAULT NULL COMMENT '鏂囧瓧澶у皬',
  `icons_margin_top` int(11) DEFAULT NULL COMMENT '瀵艰埅鍖鸿窛鎼滅储妗嗚窛绂?,
  `icons_margin_x` int(11) DEFAULT NULL COMMENT '宸﹀彸杈硅窛',
  `theme` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '涓婚妯″紡',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_config_id` (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_recommend_site` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷鐗╃悊涓婚敭',
  `site_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '绔欑偣ID',
  `category_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍒嗙被ID',
  `name` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '鍚嶇О',
  `url` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '閾炬帴',
  `icon_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '鍥炬爣绫诲瀷',
  `icon_value` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '鍥炬爣鍊?,
  `icon_color` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '鍥炬爣棰滆壊',
  `sort_order` decimal(10,2) DEFAULT '0.00' COMMENT '鎺掑簭',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_site_id` (`site_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_recommend_widget` (
  `row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '鑷鐗╃悊涓婚敭',
  `widget_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '缁勪欢ID',
  `widget_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缁勪欢绫诲瀷',
  `widget_style` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缁勪欢鏍峰紡',
  `widget_data` varchar(2048) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缁勪欢鏁版嵁',
  `layout_x` decimal(5,2) DEFAULT NULL COMMENT '甯冨眬X',
  `layout_y` decimal(5,2) DEFAULT NULL COMMENT '甯冨眬Y',
  `layout_w` decimal(5,2) DEFAULT NULL COMMENT '甯冨眬瀹?,
  `layout_h` decimal(5,2) DEFAULT NULL COMMENT '甯冨眬楂?,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`row_id`),
  UNIQUE KEY `uk_widget_id` (`widget_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

