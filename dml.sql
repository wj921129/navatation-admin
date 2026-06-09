-- ============================================================
-- 极简网页浏览器新标签页 (Navatation) — 数据库初始化 DML
-- ============================================================
USE `navatation`;

INSERT INTO `navatation_root_user` (`user_id`, `username`, `password`, `email`, `status`)
VALUES ('U000000000000000000001', 'admin', '$2a$10$51BBmH3awGt4l6zFcVMELuB5xVszrWg7AG.hB4S/U97hA0yzdGyOW', 'admin@navatation.com', 1);

INSERT INTO `navatation_root_config` (`config_id`, `user_id`, `search_engine`, `background_image`, `background_type`, `search_box_width`, `search_box_height`, `search_box_margin_top`, `icon_size`, `icon_radius`, `icon_spacing_x`, `icon_spacing_y`, `icon_text_gap`, `text_size`, `icons_margin_top`, `icons_margin_x`, `theme`)
VALUES ('UC0000000000000000001', 'U000000000000000000001', 'google', 'https://images.unsplash.com/photo-1598439473183-42c9301db5dc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=2400', 'URL', 50, 64, 192, 64, 50, 32, 48, 12, 14, 64, 10, 'light');

INSERT INTO `navatation_recommend_config` (`config_id`, `search_engine`, `background_image`, `background_type`, `search_box_width`, `search_box_height`, `search_box_margin_top`, `icon_size`, `icon_radius`, `icon_spacing_x`, `icon_spacing_y`, `icon_text_gap`, `text_size`, `icons_margin_top`, `icons_margin_x`, `theme`)
VALUES ('RCG0000000000000000001', 'google', 'https://images.unsplash.com/photo-1598439473183-42c9301db5dc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=2400', 'URL', 50, 64, 192, 64, 50, 32, 48, 12, 14, 64, 10, 'light');

INSERT INTO `navatation_recommend_category` (`category_id`, `name`, `icon_name`, `sort_order`) VALUES
('RC1', '看视频',   'Video',        1),
('RC2', 'AI工具',   'Cpu',          2),
('RC3', 'Web开发',  'Code',         3),
('RC4', '购物',     'ShoppingBag',  4),
('RC5', '新闻资讯', 'Newspaper',    5),
('RC6', '游戏',     'Gamepad2',     6),
('RC7', '音乐',     'Music',        7),
('RC8', '办公效率', 'Briefcase',    8);

INSERT INTO `navatation_recommend_site` (`site_id`, `category_id`, `name`, `url`, `icon_type`, `icon_value`, `icon_color`, `sort_order`) VALUES
-- 看视频 (RC1)
('RS1', 'RC1', 'YouTube',  'https://youtube.com',  'BUILTIN', 'Video',    '#FF0000', 1),
('RS2', 'RC1', 'Netflix',  'https://netflix.com',  'BUILTIN', 'Video',    '#E50914', 2),
('RS3', 'RC1', 'Bilibili', 'https://bilibili.com', 'BUILTIN', 'Video',    '#00A1D6', 3),
('RS4', 'RC1', 'Twitch',   'https://twitch.tv',    'BUILTIN', 'Video',    '#9146FF', 4),
('RS5', 'RC1', '腾讯视频', 'https://v.qq.com',     'BUILTIN', 'Video',    '#FF8200', 5),
('RS6', 'RC1', '爱奇艺',   'https://iqiyi.com',    'BUILTIN', 'Video',    '#00CC00', 6),
('RS7', 'RC1', '优酷',     'https://youku.com',    'BUILTIN', 'Video',    '#1A90FF', 7),
('RS8', 'RC1', '抖音',     'https://douyin.com',   'BUILTIN', 'Video',    '#111111', 8),

-- AI工具 (RC2)
('RS9', 'RC2', 'ChatGPT',    'https://chat.openai.com',  'BUILTIN', 'Cpu',    '#10A37F', 1),
('RS10', 'RC2', 'Claude',     'https://claude.ai',         'BUILTIN', 'Cpu',    '#CC9B7A', 2),
('RS11', 'RC2', 'DeepSeek',   'https://chat.deepseek.com', 'BUILTIN', 'Cpu',    '#1254FF', 3),
('RS12', 'RC2', 'Gemini',     'https://gemini.google.com', 'BUILTIN', 'Cpu',    '#4285F4', 4),
('RS13', 'RC2', '豆包',       'https://doubao.com',        'BUILTIN', 'Cpu',    '#0057FF', 5),
('RS14', 'RC2', 'Kimi',       'https://kimi.moonshot.cn',  'BUILTIN', 'Cpu',    '#5C5CFF', 6),
('RS15', 'RC2', '文心一言',   'https://yiyan.baidu.com',   'BUILTIN', 'Cpu',    '#2932E1', 7),
('RS16', 'RC2', '智谱清言',   'https://chatglm.cn',        'BUILTIN', 'Cpu',    '#3D52F5', 8),

-- Web开发 (RC3)
('RS17', 'RC3', 'GitHub',         'https://github.com',         'BUILTIN', 'Code',     '#181717', 1),
('RS18', 'RC3', 'Stack Overflow', 'https://stackoverflow.com',  'BUILTIN', 'Code',     '#F58025', 2),
('RS19', 'RC3', 'MDN',            'https://developer.mozilla.org', 'BUILTIN', 'BookOpen', '#000000', 3),
('RS20', 'RC3', 'Gitee',          'https://gitee.com',          'BUILTIN', 'Code',     '#C71D23', 4),
('RS21', 'RC3', '掘金',           'https://juejin.cn',          'BUILTIN', 'Code',     '#1E80FF', 5),
('RS22', 'RC3', 'CSDN',           'https://csdn.net',           'BUILTIN', 'Code',     '#E2231A', 6),
('RS23', 'RC3', '阿里云',         'https://aliyun.com',         'BUILTIN', 'Code',     '#FF6A00', 7),
('RS24', 'RC3', '腾讯云',         'https://cloud.tencent.com',  'BUILTIN', 'Code',     '#00A4FF', 8),

-- 购物 (RC4)
('RS25', 'RC4', '淘宝',       'https://taobao.com',      'BUILTIN', 'ShoppingBag', '#FF6A00', 1),
('RS26', 'RC4', '京东',       'https://jd.com',          'BUILTIN', 'ShoppingBag', '#E3393C', 2),
('RS27', 'RC4', '拼多多',     'https://pinduoduo.com',   'BUILTIN', 'ShoppingBag', '#E02E24', 3),
('RS28', 'RC4', '唯品会',     'https://vip.com',         'BUILTIN', 'ShoppingBag', '#F10180', 4),
('RS29', 'RC4', '美团',       'https://meituan.com',     'BUILTIN', 'ShoppingBag', '#FFC300', 5),
('RS30', 'RC4', 'Amazon',     'https://amazon.com',      'BUILTIN', 'ShoppingBag', '#FF9900', 6),
('RS31', 'RC4', 'eBay',       'https://ebay.com',        'BUILTIN', 'ShoppingBag', '#E53238', 7),
('RS32', 'RC4', 'AliExpress', 'https://aliexpress.com',  'BUILTIN', 'ShoppingBag', '#E62E04', 8),

-- 新闻资讯 (RC5)
('RS33', 'RC5', '知乎',     'https://zhihu.com',    'BUILTIN', 'BookOpen',  '#0084FF', 1),
('RS34', 'RC5', '微博',     'https://weibo.com',    'BUILTIN', 'Newspaper', '#E6162D', 2),
('RS35', 'RC5', '今日头条', 'https://toutiao.com',  'BUILTIN', 'Newspaper', '#F85959', 3),
('RS36', 'RC5', '澎湃新闻', 'https://thepaper.cn',  'BUILTIN', 'Newspaper', '#00AEB5', 4),
('RS37', 'RC5', '腾讯新闻', 'https://news.qq.com',  'BUILTIN', 'Newspaper', '#1E80FF', 5),
('RS38', 'RC5', 'Reddit',   'https://reddit.com',   'BUILTIN', 'Newspaper', '#FF4500', 6),
('RS39', 'RC5', 'BBC',      'https://bbc.com',      'BUILTIN', 'Newspaper', '#B71C1C', 7),
('RS40', 'RC5', 'Medium',   'https://medium.com',   'BUILTIN', 'BookOpen',  '#000000', 8),

-- 游戏 (RC6)
('RS41', 'RC6', 'Steam',    'https://store.steampowered.com', 'BUILTIN', 'Gamepad2', '#171A21', 1),
('RS42', 'RC6', 'Epic Games','https://epicgames.com',           'BUILTIN', 'Gamepad2', '#313131', 2),
('RS43', 'RC6', 'TapTap',   'https://taptap.cn',               'BUILTIN', 'Gamepad2', '#00D1A1', 3),
('RS44', 'RC6', '4399',     'https://4399.com',                'BUILTIN', 'Gamepad2', '#FF7700', 4),
('RS45', 'RC6', 'NGA',      'https://nga.cn',                  'BUILTIN', 'Gamepad2', '#7E1111', 5),
('RS46', 'RC6', '游民星空', 'https://gamersky.com',            'BUILTIN', 'Gamepad2', '#1C7430', 6),
('RS47', 'RC6', '3DM',      'https://3dmgame.com',             'BUILTIN', 'Gamepad2', '#FF2626', 7),
('RS48', 'RC6', 'Discord',  'https://discord.com',             'BUILTIN', 'Gamepad2', '#5865F2', 8),

-- 音乐 (RC7)
('RS49', 'RC7', '网易云音乐',   'https://music.163.com',     'BUILTIN', 'Music',    '#E60026', 1),
('RS50', 'RC7', 'QQ音乐',      'https://y.qq.com',          'BUILTIN', 'Music',    '#2CAF6F', 2),
('RS51', 'RC7', '酷狗音乐',    'https://kugou.com',         'BUILTIN', 'Music',    '#00A9FF', 3),
('RS52', 'RC7', '咪咕音乐',    'https://music.migu.cn',     'BUILTIN', 'Music',    '#FF007F', 4),
('RS53', 'RC7', 'Spotify',     'https://spotify.com',       'BUILTIN', 'Music',    '#1DB954', 5),
('RS54', 'RC7', 'Apple Music', 'https://music.apple.com',   'BUILTIN', 'Music',    '#FA243C', 6),
('RS55', 'RC7', 'SoundCloud',  'https://soundcloud.com',    'BUILTIN', 'Music',    '#FF5500', 7),
('RS56', 'RC7', 'YouTube Music','https://music.youtube.com', 'BUILTIN', 'Music',    '#FF0000', 8),

-- 办公效率 (RC8)
('RS57', 'RC8', '飞书',     'https://feishu.cn',  'BUILTIN', 'Briefcase', '#00D1A1', 1),
('RS58', 'RC8', '钉钉',     'https://dingtalk.com','BUILTIN', 'Briefcase', '#0089FF', 2),
('RS59', 'RC8', '语雀',     'https://yuque.com',  'BUILTIN', 'Briefcase', '#00B96B', 3),
('RS60', 'RC8', '腾讯文档', 'https://docs.qq.com', 'BUILTIN', 'Briefcase', '#007BFF', 4),
('RS61', 'RC8', 'WPS',      'https://wps.cn',     'BUILTIN', 'Briefcase', '#D9383A', 5),
('RS62', 'RC8', 'Notion',   'https://notion.so',  'BUILTIN', 'Briefcase', '#000000', 6),
('RS63', 'RC8', 'Figma',    'https://figma.com',  'BUILTIN', 'Briefcase', '#F24E1E', 7),
('RS64', 'RC8', 'Slack',    'https://slack.com',  'BUILTIN', 'Briefcase', '#4A154B', 8);
