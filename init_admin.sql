-- 初始化 u_admin_001 的首页网址推荐表数据
INSERT IGNORE INTO navatation_recommend_home_shortcut VALUES 
(1,'HS000000000000000000000000000001','Bilibili','https://www.bilibili.com','BUILTIN','Bilibili','#00a1d6',1.0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
(2,'HS000000000000000000000000000002','GitHub','https://github.com','BUILTIN','Github','#24292e',2.0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
(3,'HS000000000000000000000000000003','Google','https://www.google.com','BUILTIN','Google','#4285f4',3.0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
(4,'HS000000000000000000000000000004','YouTube','https://www.youtube.com','BUILTIN','Youtube','#ff0000',4.0,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

-- 初始化 u_admin_001 的其他用户表数据
INSERT IGNORE INTO navatation_user_config (config_id, user_id, search_engine, background_image, background_type, search_box_width, search_box_height, search_box_margin_top, icon_size, icon_radius, icon_spacing_x, icon_spacing_y, icon_text_gap, text_size, icons_margin_top, icons_margin_x, theme)
SELECT CONCAT('UC_A_', config_id), 'u_admin_001', search_engine, background_image, background_type, search_box_width, search_box_height, search_box_margin_top, icon_size, icon_radius, icon_spacing_x, icon_spacing_y, icon_text_gap, text_size, icons_margin_top, icons_margin_x, theme FROM navatation_recommend_config LIMIT 1;

INSERT IGNORE INTO navatation_nav_category (category_id, user_id, name, sort_order)
SELECT CONCAT('AC_', category_id), 'u_admin_001', name, sort_order FROM navatation_recommend_category;

INSERT IGNORE INTO navatation_nav_shortcut (shortcut_id, category_id, user_id, name, url, icon_type, icon_value, icon_color, sort_order, click_count)
SELECT CONCAT('AS_', shortcut_id), CONCAT('AC_', category_id), 'u_admin_001', name, url, icon_type, icon_value, icon_color, sort_order, 0 FROM navatation_recommend_shortcut;

INSERT IGNORE INTO navatation_user_widget (widget_id, user_id, type, style, x, y, meta)
SELECT CONCAT('AW_', widget_id), 'u_admin_001', type, style, x, y, meta FROM navatation_recommend_widget;

INSERT IGNORE INTO navatation_todo_item (todo_id, user_id, content, completed, sort_order)
SELECT CONCAT('AT_', todo_id), 'u_admin_001', content, completed, sort_order FROM navatation_recommend_todo_item;

INSERT IGNORE INTO navatation_nav_home_shortcut (shortcut_id, user_id, name, url, icon_type, icon_value, icon_color, sort_order, click_count)
SELECT CONCAT('AH_', shortcut_id), 'u_admin_001', name, url, icon_type, icon_value, icon_color, sort_order, 0 FROM navatation_recommend_home_shortcut;
