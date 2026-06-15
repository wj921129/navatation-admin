-- Migration: sync live DB with updated DDL (MySQL 5.7 compatible)

-- 1. Add deleted column to all tables
ALTER TABLE navatation_user ADD COLUMN deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常, 1-已删除';
ALTER TABLE navatation_user_config ADD COLUMN deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常, 1-已删除';
ALTER TABLE navatation_nav_category ADD COLUMN deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常, 1-已删除';
ALTER TABLE navatation_nav_home_shortcut ADD COLUMN deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常, 1-已删除';
ALTER TABLE navatation_todo_item ADD COLUMN deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常, 1-已删除';
ALTER TABLE navatation_user_widget ADD COLUMN deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常, 1-已删除';
ALTER TABLE navatation_recommend_config ADD COLUMN deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常, 1-已删除';
ALTER TABLE navatation_recommend_category ADD COLUMN deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常, 1-已删除';
ALTER TABLE navatation_recommend_shortcut ADD COLUMN deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常, 1-已删除';
ALTER TABLE navatation_recommend_todo_item ADD COLUMN deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常, 1-已删除';
ALTER TABLE navatation_recommend_widget ADD COLUMN deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常, 1-已删除';
ALTER TABLE navatation_recommend_home_shortcut ADD COLUMN deleted tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-正常, 1-已删除';

-- 2. Change sort_order from DOUBLE to DECIMAL(10,2)
ALTER TABLE navatation_nav_category MODIFY COLUMN sort_order decimal(10,2) NOT NULL DEFAULT '0.00';
ALTER TABLE navatation_nav_home_shortcut MODIFY COLUMN sort_order decimal(10,2) NOT NULL DEFAULT '0.00';
ALTER TABLE navatation_todo_item MODIFY COLUMN sort_order decimal(10,2) NOT NULL DEFAULT '0.00';
ALTER TABLE navatation_recommend_category MODIFY COLUMN sort_order decimal(10,2) NOT NULL DEFAULT '0.00';
ALTER TABLE navatation_recommend_shortcut MODIFY COLUMN sort_order decimal(10,2) NOT NULL DEFAULT '0.00';
ALTER TABLE navatation_recommend_todo_item MODIFY COLUMN sort_order decimal(10,2) NOT NULL DEFAULT '0.00';
ALTER TABLE navatation_recommend_home_shortcut MODIFY COLUMN sort_order decimal(10,2) NOT NULL DEFAULT '0.00';

-- 3. Remove completed/completed_at from recommend_todo_item
ALTER TABLE navatation_recommend_todo_item DROP COLUMN completed;
ALTER TABLE navatation_recommend_todo_item DROP COLUMN completed_at;

-- 4. Change widget meta from VARCHAR to JSON
ALTER TABLE navatation_user_widget MODIFY COLUMN meta json DEFAULT NULL COMMENT '组件配置JSON';
ALTER TABLE navatation_recommend_widget MODIFY COLUMN meta json DEFAULT NULL COMMENT '组件配置JSON';

-- 5. Add category_id to nav_home_shortcut
ALTER TABLE navatation_nav_home_shortcut ADD COLUMN category_id varchar(64) DEFAULT NULL COMMENT '分类ID，NULL表示不属于任何分类' AFTER shortcut_id;
ALTER TABLE navatation_nav_home_shortcut ADD INDEX idx_category_id (category_id);

-- 6. Migrate data from old nav_shortcut to nav_home_shortcut, then drop
INSERT IGNORE INTO navatation_nav_home_shortcut (shortcut_id, category_id, user_id, name, url, icon_type, icon_value, icon_color, sort_order, click_count, last_click_at, created_at, updated_at)
SELECT shortcut_id, category_id, user_id, name, url, icon_type, icon_value, icon_color, sort_order, click_count, last_click_at, created_at, updated_at FROM navatation_nav_shortcut;

DROP TABLE IF EXISTS navatation_nav_shortcut;

-- 7. 清理 admin 用户在普通用户表中的冗余数据（admin 操作已自动路由到 recommend 表）
DELETE FROM navatation_user_config WHERE user_id = 'u_admin_001';
DELETE FROM navatation_nav_category WHERE user_id = 'u_admin_001';
DELETE FROM navatation_nav_home_shortcut WHERE user_id = 'u_admin_001';
DELETE FROM navatation_user_widget WHERE user_id = 'u_admin_001';
DELETE FROM navatation_todo_item WHERE user_id = 'u_admin_001';
