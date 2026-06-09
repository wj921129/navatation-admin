-- ============================================================
-- 鏋佺畝缃戦〉娴忚鍣ㄦ柊鏍囩椤?(Navatation) 鈥?鏁版嵁搴撳垵濮嬪寲 DDL
-- ============================================================
DROP DATABASE IF EXISTS `navatation`;
CREATE DATABASE `navatation` DEFAULT CHARACTER SET utf8mb4 DEFAULT COLLATE utf8mb4_unicode_ci;
USE `navatation`;

CREATE TABLE IF NOT EXISTS `navatation_user` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '鐢ㄦ埛ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑U鐨勯殢鏈篣UID',
    `username`       VARCHAR(20)           NOT NULL                COMMENT '鐢ㄦ埛鍚嶏紝3-20瀛楃锛屽敮涓€',
    `password`       VARCHAR(128)          NOT NULL                COMMENT '瀵嗙爜锛孊Crypt 鍔犲瘑瀛樺偍',
    `email`          VARCHAR(128)          DEFAULT NULL            COMMENT '閭锛岀敤浜庡瘑鐮佹壘鍥?,
    `avatar`         VARCHAR(512)          DEFAULT NULL            COMMENT '澶村儚URL锛孫SS鍦板潃',
    `status`         TINYINT               NOT NULL DEFAULT 1      COMMENT '璐﹀彿鐘舵€侊細0-绂佺敤, 1-姝ｅ父',
    `role`           VARCHAR(16)           NOT NULL DEFAULT 'USER' COMMENT '瑙掕壊锛歎SER-鏅€氱敤鎴? ADMIN-瓒呯骇绠＄悊鍛?,
    `last_login_at`  DATETIME              DEFAULT NULL            COMMENT '鏈€鍚庣櫥褰曟椂闂?,
    `last_login_ip`  VARCHAR(45)           DEFAULT NULL            COMMENT '鏈€鍚庣櫥褰旾P',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_nav_category` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '鍒嗙被ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑CG鐨勯殢鏈篣UID',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '鎵€灞炵敤鎴稩D锛屽叧鑱?navatation_user.user_id',
    `name`           VARCHAR(32)           NOT NULL                COMMENT '鍒嗙被鍚嶇О锛屽锛氬父鐢ㄣ€佸伐浣溿€佸ū涔?,
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '鎺掑簭搴忓彿锛岃秺灏忚秺闈犲墠',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_category_id` (`category_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_sort` (`user_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_nav_shortcut` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `shortcut_id`    VARCHAR(64)           NOT NULL                COMMENT '蹇嵎鏂瑰紡ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑SC鐨勯殢鏈篣UID',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '鎵€灞炲垎绫籌D锛屽叧鑱?navatation_nav_category.category_id',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '鎵€灞炵敤鎴稩D锛屽叧鑱?navatation_user.user_id锛堝啑浣欏瓧娈碉紝鍔犻€熸煡璇級',
    `name`           VARCHAR(64)           NOT NULL                COMMENT '缃戠珯鍚嶇О锛屾樉绀哄湪鍥炬爣涓嬫柟',
    `url`            VARCHAR(2048)         NOT NULL                COMMENT '缃戠珯URL锛岀偣鍑昏烦杞洰鏍?,
    `icon_type`      VARCHAR(16)           NOT NULL DEFAULT 'BUILTIN' COMMENT '鍥炬爣绫诲瀷',
    `icon_value`     VARCHAR(2048)         DEFAULT NULL            COMMENT '鍥炬爣鍊?,
    `icon_color`     VARCHAR(7)            DEFAULT NULL            COMMENT '鍥炬爣棰滆壊',
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '鎺掑簭搴忓彿',
    `click_count`    BIGINT       UNSIGNED NOT NULL DEFAULT 0      COMMENT '鐐瑰嚮娆℃暟',
    `last_click_at`  DATETIME              DEFAULT NULL            COMMENT '鏈€鍚庣偣鍑绘椂闂?,
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_shortcut_id` (`shortcut_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_category_sort` (`user_id`, `category_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_user_config` (
    `row_id`                  BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `config_id`               VARCHAR(64)           NOT NULL                COMMENT '閰嶇疆ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑UC鐨勯殢鏈篣UID',
    `user_id`                 VARCHAR(64)           NOT NULL                COMMENT '鎵€灞炵敤鎴稩D锛屽叧鑱?navatation_user.user_id锛屽敮涓€',
    `search_engine`           VARCHAR(16)           NOT NULL DEFAULT 'google' COMMENT '榛樿鎼滅储寮曟搸',
    `background_image`        VARCHAR(2048)         DEFAULT NULL            COMMENT '澹佺焊URL',
    `background_type`         VARCHAR(16)           NOT NULL DEFAULT 'URL'  COMMENT '澹佺焊绫诲瀷',
    `search_box_width`        INT                   NOT NULL DEFAULT 50     COMMENT '鎼滅储妗嗗搴﹀睆骞曞崰姣?%)',
    `search_box_height`       INT                   NOT NULL DEFAULT 64     COMMENT '鎼滅储妗嗛珮搴﹀儚绱?,
    `search_box_margin_top`   INT                   NOT NULL DEFAULT 192    COMMENT '鎼滅储妗嗚窛椤堕儴璺濈鍍忕礌',
    `icon_size`               INT                   NOT NULL DEFAULT 64     COMMENT '鍥炬爣澶у皬鍍忕礌',
    `icon_radius`             INT                   NOT NULL DEFAULT 50     COMMENT '鍥炬爣鍦嗚鐧惧垎姣?,
    `icon_spacing_x`          INT                   NOT NULL DEFAULT 32     COMMENT '鍥炬爣姘村钩闂磋窛鍍忕礌',
    `icon_spacing_y`          INT                   NOT NULL DEFAULT 48     COMMENT '鍥炬爣鍨傜洿闂磋窛鍍忕礌',
    `icon_text_gap`           INT                   NOT NULL DEFAULT 12     COMMENT '鍥炬爣涓庢枃瀛楅棿璺濆儚绱?,
    `text_size`               INT                   NOT NULL DEFAULT 14     COMMENT '鏂囧瓧澶у皬鍍忕礌',
    `icons_margin_top`        INT                   NOT NULL DEFAULT 64     COMMENT '瀵艰埅鍖鸿窛鎼滅储妗嗚窛绂诲儚绱?,
    `icons_margin_x`          INT                   NOT NULL DEFAULT 10     COMMENT '瀵艰埅鍖哄乏鍙宠竟璺濈櫨鍒嗘瘮',
    `theme`                   VARCHAR(16)           NOT NULL DEFAULT 'dark' COMMENT '涓婚妯″紡',
    `created_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_config_id` (`config_id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_todo_item` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `todo_id`        VARCHAR(64)           NOT NULL                COMMENT '寰呭姙ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑TD鐨勯殢鏈篣UID',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '鎵€灞炵敤鎴稩D锛屽叧鑱?navatation_user.user_id',
    `content`        VARCHAR(512)          NOT NULL                COMMENT '寰呭姙鍐呭',
    `completed`      TINYINT(1)            NOT NULL DEFAULT 0      COMMENT '瀹屾垚鐘舵€?,
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '鎺掑簭搴忓彿',
    `completed_at`   DATETIME              DEFAULT NULL            COMMENT '瀹屾垚鏃堕棿',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_todo_id` (`todo_id`),
    KEY `idx_user_completed` (`user_id`, `completed`),
    KEY `idx_user_sort` (`user_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_recommend_category` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '鎺ㄨ崘鍒嗙被ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑RC鐨勯殢鏈篣UID',
    `name`           VARCHAR(32)           NOT NULL                COMMENT '鍒嗙被鍚嶇О',
    `icon_name`      VARCHAR(32)           NOT NULL                COMMENT '鍒嗙被鍥炬爣鍚?,
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '鎺掑簭搴忓彿',
    `status`         TINYINT               NOT NULL DEFAULT 1      COMMENT '鐘舵€侊細0-绂佺敤, 1-鍚敤',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_category_id` (`category_id`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_recommend_site` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `site_id`        VARCHAR(64)           NOT NULL                COMMENT '鎺ㄨ崘缃戠珯ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑RS鐨勯殢鏈篣UID',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '鎵€灞炴帹鑽愬垎绫籌D锛屽叧鑱?navatation_recommend_category.category_id',
    `name`           VARCHAR(64)           NOT NULL                COMMENT '缃戠珯鍚嶇О',
    `url`            VARCHAR(2048)         NOT NULL                COMMENT '缃戠珯URL',
    `icon_type`      VARCHAR(16)           NOT NULL DEFAULT 'BUILTIN' COMMENT '鍥炬爣绫诲瀷',
    `icon_value`     VARCHAR(2048)         DEFAULT NULL            COMMENT '鍥炬爣鍊?,
    `icon_color`     VARCHAR(7)            DEFAULT NULL            COMMENT '鍥炬爣棰滆壊',
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '鎺掑簭搴忓彿',
    `status`         TINYINT               NOT NULL DEFAULT 1      COMMENT '鐘舵€侊細0-绂佺敤, 1-鍚敤',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_site_id` (`site_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_category_sort` (`category_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_recommend_config` (
    `row_id`                  BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `config_id`               VARCHAR(64)           NOT NULL                COMMENT '閰嶇疆ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑RCG鐨勯殢鏈篣UID',
    `search_engine`           VARCHAR(16)           NOT NULL DEFAULT 'google' COMMENT '榛樿鎼滅储寮曟搸',
    `background_image`        VARCHAR(2048)         DEFAULT NULL            COMMENT '澹佺焊URL',
    `background_type`         VARCHAR(16)           NOT NULL DEFAULT 'URL'  COMMENT '澹佺焊绫诲瀷',
    `search_box_width`        INT                   NOT NULL DEFAULT 50     COMMENT '鎼滅储妗嗗搴﹀睆骞曞崰姣?%)',
    `search_box_height`       INT                   NOT NULL DEFAULT 64     COMMENT '鎼滅储妗嗛珮搴﹀儚绱?,
    `search_box_margin_top`   INT                   NOT NULL DEFAULT 192    COMMENT '鎼滅储妗嗚窛椤堕儴璺濈鍍忕礌',
    `icon_size`               INT                   NOT NULL DEFAULT 64     COMMENT '鍥炬爣澶у皬鍍忕礌',
    `icon_radius`             INT                   NOT NULL DEFAULT 50     COMMENT '鍥炬爣鍦嗚鐧惧垎姣?,
    `icon_spacing_x`          INT                   NOT NULL DEFAULT 32     COMMENT '鍥炬爣姘村钩闂磋窛鍍忕礌',
    `icon_spacing_y`          INT                   NOT NULL DEFAULT 48     COMMENT '鍥炬爣鍨傜洿闂磋窛鍍忕礌',
    `icon_text_gap`           INT                   NOT NULL DEFAULT 12     COMMENT '鍥炬爣涓庢枃瀛楅棿璺濆儚绱?,
    `text_size`               INT                   NOT NULL DEFAULT 14     COMMENT '鏂囧瓧澶у皬鍍忕礌',
    `icons_margin_top`        INT                   NOT NULL DEFAULT 64     COMMENT '瀵艰埅鍖鸿窛鎼滅储妗嗚窛绂诲儚绱?,
    `icons_margin_x`          INT                   NOT NULL DEFAULT 10     COMMENT '瀵艰埅鍖哄乏鍙宠竟璺濈櫨鍒嗘瘮',
    `theme`                   VARCHAR(16)           NOT NULL DEFAULT 'dark' COMMENT '涓婚妯″紡',
    `created_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_config_id` (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_user_widget` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `widget_id`      VARCHAR(64)           NOT NULL                COMMENT '缁勪欢ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑WG鐨勯殢鏈?2浣嶇函鏁板瓧瀛楃涓?,
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '鎵€灞炵敤鎴稩D锛屽叧鑱?navatation_user.user_id',
    `type`           VARCHAR(32)           NOT NULL                COMMENT '缁勪欢绫诲瀷锛屽锛歝lock, weather, calendar',
    `style`          VARCHAR(32)           NOT NULL                COMMENT '缁勪欢鏍峰紡锛屽锛歛nalog, digital, flip, traditional',
    `x`              DECIMAL(5, 2)         NOT NULL                COMMENT 'X杞寸櫨鍒嗘瘮浣嶇疆 (0.00 - 100.00)',
    `y`              DECIMAL(5, 2)         NOT NULL                COMMENT 'Y杞寸櫨鍒嗘瘮浣嶇疆 (0.00 - 100.00)',
    `meta`           VARCHAR(2048)         DEFAULT NULL            COMMENT '鍙€夊厓鏁版嵁锛孞SON瀛楃涓?,
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_widget_id` (`widget_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_root_config` (
    `row_id`                  BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `config_id`               VARCHAR(64)           NOT NULL                COMMENT '閰嶇疆ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑UC鐨勯殢鏈篣UID',
    `user_id`                 VARCHAR(64)           NOT NULL                COMMENT '鎵€灞炵敤鎴稩D锛屽叧鑱?navatation_user.user_id锛屽敮涓€',
    `search_engine`           VARCHAR(16)           NOT NULL DEFAULT 'google' COMMENT '榛樿鎼滅储寮曟搸',
    `background_image`        VARCHAR(2048)         DEFAULT NULL            COMMENT '澹佺焊URL',
    `background_type`         VARCHAR(16)           NOT NULL DEFAULT 'URL'  COMMENT '澹佺焊绫诲瀷',
    `search_box_width`        INT                   NOT NULL DEFAULT 50     COMMENT '鎼滅储妗嗗搴﹀睆骞曞崰姣?%)',
    `search_box_height`       INT                   NOT NULL DEFAULT 64     COMMENT '鎼滅储妗嗛珮搴﹀儚绱?,
    `search_box_margin_top`   INT                   NOT NULL DEFAULT 192    COMMENT '鎼滅储妗嗚窛椤堕儴璺濈鍍忕礌',
    `icon_size`               INT                   NOT NULL DEFAULT 64     COMMENT '鍥炬爣澶у皬鍍忕礌',
    `icon_radius`             INT                   NOT NULL DEFAULT 50     COMMENT '鍥炬爣鍦嗚鐧惧垎姣?,
    `icon_spacing_x`          INT                   NOT NULL DEFAULT 32     COMMENT '鍥炬爣姘村钩闂磋窛鍍忕礌',
    `icon_spacing_y`          INT                   NOT NULL DEFAULT 48     COMMENT '鍥炬爣鍨傜洿闂磋窛鍍忕礌',
    `icon_text_gap`           INT                   NOT NULL DEFAULT 12     COMMENT '鍥炬爣涓庢枃瀛楅棿璺濆儚绱?,
    `text_size`               INT                   NOT NULL DEFAULT 14     COMMENT '鏂囧瓧澶у皬鍍忕礌',
    `icons_margin_top`        INT                   NOT NULL DEFAULT 64     COMMENT '瀵艰埅鍖鸿窛鎼滅储妗嗚窛绂诲儚绱?,
    `icons_margin_x`          INT                   NOT NULL DEFAULT 10     COMMENT '瀵艰埅鍖哄乏鍙宠竟璺濈櫨鍒嗘瘮',
    `theme`                   VARCHAR(16)           NOT NULL DEFAULT 'dark' COMMENT '涓婚妯″紡',
    `created_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`              DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_config_id` (`config_id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_root_nav_category` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '鍒嗙被ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑CG鐨勯殢鏈篣UID',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '鎵€灞炵敤鎴稩D锛屽叧鑱?navatation_user.user_id',
    `name`           VARCHAR(32)           NOT NULL                COMMENT '鍒嗙被鍚嶇О锛屽锛氬父鐢ㄣ€佸伐浣溿€佸ū涔?,
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '鎺掑簭搴忓彿锛岃秺灏忚秺闈犲墠',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_category_id` (`category_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_sort` (`user_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_root_nav_shortcut` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `shortcut_id`    VARCHAR(64)           NOT NULL                COMMENT '蹇嵎鏂瑰紡ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑SC鐨勯殢鏈篣UID',
    `category_id`    VARCHAR(64)           NOT NULL                COMMENT '鎵€灞炲垎绫籌D锛屽叧鑱?navatation_root_nav_category.category_id',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '鎵€灞炵敤鎴稩D锛屽叧鑱?navatation_user.user_id锛堝啑浣欏瓧娈碉紝鍔犻€熸煡璇級',
    `name`           VARCHAR(64)           NOT NULL                COMMENT '缃戠珯鍚嶇О锛屾樉绀哄湪鍥炬爣涓嬫柟',
    `url`            VARCHAR(2048)         NOT NULL                COMMENT '缃戠珯URL锛岀偣鍑昏烦杞洰鏍?,
    `icon_type`      VARCHAR(16)           NOT NULL DEFAULT 'BUILTIN' COMMENT '鍥炬爣绫诲瀷',
    `icon_value`     VARCHAR(2048)         DEFAULT NULL            COMMENT '鍥炬爣鍊?,
    `icon_color`     VARCHAR(7)            DEFAULT NULL            COMMENT '鍥炬爣棰滆壊',
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '鎺掑簭搴忓彿',
    `click_count`    BIGINT       UNSIGNED NOT NULL DEFAULT 0      COMMENT '鐐瑰嚮娆℃暟',
    `last_click_at`  DATETIME              DEFAULT NULL            COMMENT '鏈€鍚庣偣鍑绘椂闂?,
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_shortcut_id` (`shortcut_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_category_sort` (`user_id`, `category_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_root_user_widget` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `widget_id`      VARCHAR(64)           NOT NULL                COMMENT '缁勪欢ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑WG鐨勯殢鏈?2浣嶇函鏁板瓧瀛楃涓?,
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '鎵€灞炵敤鎴稩D锛屽叧鑱?navatation_user.user_id',
    `type`           VARCHAR(32)           NOT NULL                COMMENT '缁勪欢绫诲瀷锛屽锛歝lock, weather, calendar',
    `style`          VARCHAR(32)           NOT NULL                COMMENT '缁勪欢鏍峰紡锛屽锛歛nalog, digital, flip, traditional',
    `x`              DECIMAL(5, 2)         NOT NULL                COMMENT 'X杞寸櫨鍒嗘瘮浣嶇疆 (0.00 - 100.00)',
    `y`              DECIMAL(5, 2)         NOT NULL                COMMENT 'Y杞寸櫨鍒嗘瘮浣嶇疆 (0.00 - 100.00)',
    `meta`           VARCHAR(2048)         DEFAULT NULL            COMMENT '鍙€夊厓鏁版嵁锛孞SON瀛楃涓?,
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_widget_id` (`widget_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_user_type` (`user_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `navatation_root_todo_item` (
    `row_id`         BIGINT       UNSIGNED NOT NULL AUTO_INCREMENT  COMMENT '鑷鐗╃悊涓婚敭',
    `todo_id`        VARCHAR(64)           NOT NULL                COMMENT '寰呭姙ID锛屼笟鍔￠€昏緫涓婚敭锛屽甫鍓嶇紑TD鐨勯殢鏈篣UID',
    `user_id`        VARCHAR(64)           NOT NULL                COMMENT '鎵€灞炵敤鎴稩D锛屽叧鑱?navatation_user.user_id',
    `content`        VARCHAR(512)          NOT NULL                COMMENT '寰呭姙鍐呭',
    `completed`      TINYINT(1)            NOT NULL DEFAULT 0      COMMENT '瀹屾垚鐘舵€?,
    `sort_order`     DOUBLE                NOT NULL DEFAULT 0.0    COMMENT '鎺掑簭搴忓彿',
    `completed_at`   DATETIME              DEFAULT NULL            COMMENT '瀹屾垚鏃堕棿',
    `created_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updated_at`     DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    PRIMARY KEY (`row_id`),
    UNIQUE KEY `uk_todo_id` (`todo_id`),
    KEY `idx_user_completed` (`user_id`, `completed`),
    KEY `idx_user_sort` (`user_id`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

