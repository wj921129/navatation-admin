ALTER TABLE `navatation_nav_category` MODIFY COLUMN `sort_order` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '排序序号，越小越靠前';
ALTER TABLE `navatation_nav_shortcut` MODIFY COLUMN `sort_order` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '排序序号';
ALTER TABLE `navatation_todo_item` MODIFY COLUMN `sort_order` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '排序序号';
ALTER TABLE `navatation_recommend_category` MODIFY COLUMN `sort_order` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '排序序号';
ALTER TABLE `navatation_recommend_site` MODIFY COLUMN `sort_order` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '排序序号';
