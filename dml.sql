-- DML: Navatation 数据初始化脚本
-- 适用于 MySQL 5.7+

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS=0;

-- ============================================
-- 1. 清空所有表
-- ============================================
TRUNCATE TABLE navatation_user_widget;
TRUNCATE TABLE navatation_todo_item;
TRUNCATE TABLE navatation_nav_home_shortcut;
TRUNCATE TABLE navatation_nav_category;
TRUNCATE TABLE navatation_user_config;
TRUNCATE TABLE navatation_user;
TRUNCATE TABLE navatation_recommend_home_shortcut;
TRUNCATE TABLE navatation_recommend_widget;
TRUNCATE TABLE navatation_recommend_todo_item;
TRUNCATE TABLE navatation_recommend_shortcut;
TRUNCATE TABLE navatation_recommend_category;
TRUNCATE TABLE navatation_recommend_config;

-- ============================================
-- 2. Recommend 推荐模板数据（Admin 管理，游客使用）
-- ============================================

-- 推荐配置
INSERT INTO navatation_recommend_config
  (config_id, search_engine, background_image, background_type, search_box_width, search_box_height, search_box_margin_top, icon_size, icon_radius, icon_spacing_x, icon_spacing_y, icon_text_gap, text_size, icons_margin_top, icons_margin_x, theme, deleted)
VALUES
  ('RCG0000000000000000001', 'google', 'https://images.unsplash.com/photo-1598439473183-42c9301db5dc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=2400', 'URL', 50, 64, 192, 64, 50, 32, 48, 12, 14, 64, 10, 'light', 0);

-- 推荐分类（8个）
INSERT INTO navatation_recommend_category (category_id, name, sort_order, deleted) VALUES
  ('RC1', '看视频', 0, 0),
  ('RC2', 'AI工具', 1, 0),
  ('RC3', 'Web开发', 2, 0),
  ('RC4', '购物', 3, 0),
  ('RC5', '新闻资讯', 4, 0),
  ('RC6', '游戏', 5, 0),
  ('RC7', '音乐', 6, 0),
  ('RC8', '办公效率', 7, 0);

-- 推荐快捷方式（每个分类8个，共64个）
INSERT INTO navatation_recommend_shortcut (shortcut_id, category_id, name, url, icon_type, icon_value, icon_color, sort_order, deleted) VALUES
  -- RC1 看视频
  ('RS1',  'RC1', 'YouTube',  'https://youtube.com',  'FAVICON', 'https://api.iowen.cn/favicon/youtube.com.png',  '#FF0000', 1, 0),
  ('RS2',  'RC1', 'Netflix',  'https://netflix.com',  'FAVICON', 'https://api.iowen.cn/favicon/netflix.com.png',  '#E50914', 2, 0),
  ('RS3',  'RC1', 'Bilibili', 'https://bilibili.com', 'FAVICON', 'https://api.iowen.cn/favicon/bilibili.com.png', '#00A1D6', 3, 0),
  ('RS4',  'RC1', 'Twitch',   'https://twitch.tv',    'FAVICON', 'https://api.iowen.cn/favicon/twitch.tv.png',    '#9146FF', 4, 0),
  ('RS5',  'RC1', '腾讯视频', 'https://v.qq.com',     'FAVICON', 'https://api.iowen.cn/favicon/v.qq.com.png',     '#FF8200', 5, 0),
  ('RS6',  'RC1', '爱奇艺',   'https://iqiyi.com',    'FAVICON', 'https://api.iowen.cn/favicon/iqiyi.com.png',    '#00CC00', 6, 0),
  ('RS7',  'RC1', '优酷',     'https://youku.com',    'FAVICON', 'https://www.google.com/s2/favicons?sz=64&domain=youku.com', '#1A90FF', 7, 0),
  ('RS8',  'RC1', '抖音',     'https://douyin.com',   'FAVICON', 'https://www.google.com/s2/favicons?sz=64&domain=douyin.com', '#111111', 8, 0),
  -- RC2 AI工具
  ('RS9',  'RC2', 'ChatGPT',  'https://chat.openai.com',     'FAVICON', 'https://api.iowen.cn/favicon/chat.openai.com.png', '#10A37F', 1, 0),
  ('RS10', 'RC2', 'Claude',   'https://claude.ai',           'FAVICON', 'https://api.iowen.cn/favicon/claude.ai.png',       '#CC9B7A', 2, 0),
  ('RS11', 'RC2', 'DeepSeek', 'https://chat.deepseek.com',   'FAVICON', 'https://www.google.com/s2/favicons?sz=64&domain=chat.deepseek.com', '#1254FF', 3, 0),
  ('RS12', 'RC2', 'Gemini',   'https://gemini.google.com',   'FAVICON', 'https://api.iowen.cn/favicon/gemini.google.com.png', '#4285F4', 4, 0),
  ('RS13', 'RC2', '豆包',     'https://doubao.com',          'FAVICON', 'https://api.iowen.cn/favicon/doubao.com.png',      '#0057FF', 5, 0),
  ('RS14', 'RC2', 'Kimi',     'https://kimi.moonshot.cn',    'FAVICON', 'https://api.iowen.cn/favicon/kimi.moonshot.cn.png', '#5C5CFF', 6, 0),
  ('RS15', 'RC2', '文心一言', 'https://yiyan.baidu.com',     'FAVICON', 'https://api.iowen.cn/favicon/yiyan.baidu.com.png', '#2932E1', 7, 0),
  ('RS16', 'RC2', '智谱清言', 'https://chatglm.cn',          'FAVICON', 'https://api.iowen.cn/favicon/chatglm.cn.png',      '#3D52F5', 8, 0),
  -- RC3 Web开发
  ('RS17', 'RC3', 'GitHub',         'https://github.com',             'FAVICON', 'https://api.iowen.cn/favicon/github.com.png',             '#181717', 1, 0),
  ('RS18', 'RC3', 'Stack Overflow', 'https://stackoverflow.com',      'FAVICON', 'https://api.iowen.cn/favicon/stackoverflow.com.png',      '#F58025', 2, 0),
  ('RS19', 'RC3', 'MDN',            'https://developer.mozilla.org',  'FAVICON', 'https://api.iowen.cn/favicon/developer.mozilla.org.png',  '#000000', 3, 0),
  ('RS20', 'RC3', 'Gitee',          'https://gitee.com',              'FAVICON', 'https://api.iowen.cn/favicon/gitee.com.png',              '#C71D23', 4, 0),
  ('RS21', 'RC3', '掘金',           'https://juejin.cn',              'FAVICON', 'https://www.google.com/s2/favicons?sz=64&domain=juejin.cn',   '#1E80FF', 5, 0),
  ('RS22', 'RC3', 'CSDN',           'https://csdn.net',               'FAVICON', 'https://www.google.com/s2/favicons?sz=64&domain=csdn.net',    '#E2231A', 6, 0),
  ('RS23', 'RC3', '阿里云',         'https://aliyun.com',             'FAVICON', 'https://api.iowen.cn/favicon/aliyun.com.png',             '#FF6A00', 7, 0),
  ('RS24', 'RC3', '腾讯云',         'https://cloud.tencent.com',      'FAVICON', 'https://api.iowen.cn/favicon/cloud.tencent.com.png',      '#00A4FF', 8, 0),
  -- RC4 购物
  ('RS25', 'RC4', '淘宝',       'https://taobao.com',      'FAVICON', 'https://api.iowen.cn/favicon/taobao.com.png',      '#FF6A00', 1, 0),
  ('RS26', 'RC4', '京东',       'https://jd.com',          'FAVICON', 'https://api.iowen.cn/favicon/jd.com.png',          '#E3393C', 2, 0),
  ('RS27', 'RC4', '拼多多',     'https://pinduoduo.com',   'FAVICON', 'https://api.iowen.cn/favicon/pinduoduo.com.png',   '#E02E24', 3, 0),
  ('RS28', 'RC4', '唯品会',     'https://vip.com',         'FAVICON', 'https://api.iowen.cn/favicon/vip.com.png',         '#F10180', 4, 0),
  ('RS29', 'RC4', '美团',       'https://meituan.com',     'FAVICON', 'https://api.iowen.cn/favicon/meituan.com.png',     '#FFC300', 5, 0),
  ('RS30', 'RC4', 'Amazon',     'https://amazon.com',      'FAVICON', 'https://api.iowen.cn/favicon/amazon.com.png',      '#FF9900', 6, 0),
  ('RS31', 'RC4', 'eBay',       'https://ebay.com',        'FAVICON', 'https://api.iowen.cn/favicon/ebay.com.png',        '#E53238', 7, 0),
  ('RS32', 'RC4', 'AliExpress', 'https://aliexpress.com',  'FAVICON', 'https://api.iowen.cn/favicon/aliexpress.com.png',  '#E62E04', 8, 0),
  -- RC5 新闻资讯
  ('RS33', 'RC5', '知乎',       'https://zhihu.com',       'FAVICON', 'https://www.google.com/s2/favicons?sz=64&domain=zhihu.com',   '#0084FF', 1, 0),
  ('RS34', 'RC5', '微博',       'https://weibo.com',       'FAVICON', 'https://api.iowen.cn/favicon/weibo.com.png',             '#E6162D', 2, 0),
  ('RS35', 'RC5', '今日头条',   'https://toutiao.com',     'FAVICON', 'https://api.iowen.cn/favicon/toutiao.com.png',           '#F85959', 3, 0),
  ('RS36', 'RC5', '澎湃新闻',   'https://thepaper.cn',     'FAVICON', 'https://api.iowen.cn/favicon/thepaper.cn.png',           '#00AEB5', 4, 0),
  ('RS37', 'RC5', '腾讯新闻',   'https://news.qq.com',     'FAVICON', 'https://api.iowen.cn/favicon/news.qq.com.png',           '#1E80FF', 5, 0),
  ('RS38', 'RC5', 'Reddit',     'https://reddit.com',      'FAVICON', 'https://api.iowen.cn/favicon/reddit.com.png',            '#FF4500', 6, 0),
  ('RS39', 'RC5', 'BBC',        'https://bbc.com',         'FAVICON', 'https://api.iowen.cn/favicon/bbc.com.png',               '#B71C1C', 7, 0),
  ('RS40', 'RC5', 'Medium',     'https://medium.com',      'FAVICON', 'https://www.google.com/s2/favicons?sz=64&domain=medium.com',  '#000000', 8, 0),
  -- RC6 游戏
  ('RS41', 'RC6', 'Steam',      'https://store.steampowered.com', 'FAVICON', 'https://api.iowen.cn/favicon/store.steampowered.com.png', '#171A21', 1, 0),
  ('RS42', 'RC6', 'Epic Games', 'https://epicgames.com',   'FAVICON', 'https://api.iowen.cn/favicon/epicgames.com.png',         '#313131', 2, 0),
  ('RS43', 'RC6', 'TapTap',     'https://taptap.cn',       'FAVICON', 'https://www.google.com/s2/favicons?sz=64&domain=taptap.cn',  '#00D1A1', 3, 0),
  ('RS44', 'RC6', '4399',       'https://4399.com',        'FAVICON', 'https://api.iowen.cn/favicon/4399.com.png',              '#FF7700', 4, 0),
  ('RS45', 'RC6', 'NGA',        'https://nga.cn',          'FAVICON', 'https://api.iowen.cn/favicon/nga.cn.png',                '#7E1111', 5, 0),
  ('RS46', 'RC6', '游民星空',   'https://gamersky.com',    'FAVICON', 'https://api.iowen.cn/favicon/gamersky.com.png',          '#1C7430', 6, 0),
  ('RS47', 'RC6', '3DM',        'https://3dmgame.com',     'FAVICON', 'https://www.google.com/s2/favicons?sz=64&domain=3dmgame.com', '#FF2626', 7, 0),
  ('RS48', 'RC6', 'Discord',    'https://discord.com',     'FAVICON', 'https://api.iowen.cn/favicon/discord.com.png',           '#5865F2', 8, 0),
  -- RC7 音乐
  ('RS49', 'RC7', '网易云音乐',   'https://music.163.com',      'FAVICON', 'https://api.iowen.cn/favicon/music.163.com.png',      '#E60026', 1, 0),
  ('RS50', 'RC7', 'QQ音乐',       'https://y.qq.com',           'FAVICON', 'https://api.iowen.cn/favicon/y.qq.com.png',           '#2CAF6F', 2, 0),
  ('RS51', 'RC7', '酷狗音乐',     'https://kugou.com',          'FAVICON', 'https://api.iowen.cn/favicon/kugou.com.png',          '#00A9FF', 3, 0),
  ('RS52', 'RC7', '咪咕音乐',     'https://music.migu.cn',      'FAVICON', 'https://api.iowen.cn/favicon/music.migu.cn.png',      '#FF007F', 4, 0),
  ('RS53', 'RC7', 'Spotify',      'https://spotify.com',        'FAVICON', 'https://api.iowen.cn/favicon/spotify.com.png',        '#1DB954', 5, 0),
  ('RS54', 'RC7', 'Apple Music',  'https://music.apple.com',    'FAVICON', 'https://api.iowen.cn/favicon/music.apple.com.png',    '#FA243C', 6, 0),
  ('RS55', 'RC7', 'SoundCloud',   'https://soundcloud.com',     'FAVICON', 'https://api.iowen.cn/favicon/soundcloud.com.png',     '#FF5500', 7, 0),
  ('RS56', 'RC7', 'YouTube Music','https://music.youtube.com',  'FAVICON', 'https://api.iowen.cn/favicon/music.youtube.com.png',  '#FF0000', 8, 0),
  -- RC8 办公效率
  ('RS57', 'RC8', '飞书',       'https://feishu.cn',       'FAVICON', 'https://api.iowen.cn/favicon/feishu.cn.png',      '#00D1A1', 1, 0),
  ('RS58', 'RC8', '钉钉',       'https://dingtalk.com',    'FAVICON', 'https://api.iowen.cn/favicon/dingtalk.com.png',   '#0089FF', 2, 0),
  ('RS59', 'RC8', '语雀',       'https://yuque.com',       'FAVICON', 'https://api.iowen.cn/favicon/yuque.com.png',      '#00B96B', 3, 0),
  ('RS60', 'RC8', '腾讯文档',   'https://docs.qq.com',     'FAVICON', 'https://api.iowen.cn/favicon/docs.qq.com.png',    '#007BFF', 4, 0),
  ('RS61', 'RC8', 'WPS',        'https://wps.cn',          'FAVICON', 'https://api.iowen.cn/favicon/wps.cn.png',         '#D9383A', 5, 0),
  ('RS62', 'RC8', 'Notion',     'https://notion.so',       'FAVICON', 'https://api.iowen.cn/favicon/notion.so.png',      '#000000', 6, 0),
  ('RS63', 'RC8', 'Figma',      'https://figma.com',       'FAVICON', 'https://api.iowen.cn/favicon/figma.com.png',      '#F24E1E', 7, 0),
  ('RS64', 'RC8', 'Slack',      'https://slack.com',       'FAVICON', 'https://api.iowen.cn/favicon/slack.com.png',      '#4A154B', 8, 0);

-- 推荐待办（模板数据，无 completed 字段）
INSERT INTO navatation_recommend_todo_item (todo_id, content, sort_order, deleted) VALUES
  ('TD0000000000000000000001', '👋 欢迎使用极简导航页', 1, 0),
  ('TD0000000000000000000002', '✅ 点击完成待办事项', 2, 0),
  ('TD0000000000000000000003', '💡 提示：点击右下角按钮注册/登录，可永久云端保存您的数据哦', 3, 0);

-- 推荐组件
INSERT INTO navatation_recommend_widget (widget_id, type, style, x, y, meta, deleted) VALUES
  ('WG0000000000000000000001', 'clock',    'flip',   2.08,  4.23,  '{}', 0),
  ('WG0000000000000000000002', 'calendar', 'month',  2.08, 19.05,  '{}', 0),
  ('WG0000000000000000000003', 'weather',  'simple', 14.58,  4.23,  '{"locations":[{"name":"武汉, 湖北, 中国","lat":30.58333,"lon":114.26667}]}', 0);

-- 推荐首页快捷方式
INSERT INTO navatation_recommend_home_shortcut (shortcut_id, name, url, icon_type, icon_value, icon_color, sort_order) VALUES
  ('HS000000000000000000000000000001', 'Bilibili', 'https://www.bilibili.com', 'BUILTIN', 'Bilibili', '#00a1d6', 1),
  ('HS000000000000000000000000000002', 'GitHub',   'https://github.com',       'BUILTIN', 'Github',   '#24292e', 2),
  ('HS000000000000000000000000000003', 'Google',   'https://www.google.com',   'BUILTIN', 'Google',   '#4285f4', 3),
  ('HS000000000000000000000000000004', 'YouTube',  'https://www.youtube.com',  'BUILTIN', 'Youtube',  '#ff0000', 4);

-- ============================================
-- 3. Admin 用户数据初始化
-- ============================================

-- 管理员账户 (密码: admin123)
-- admin 的所有 CRUD 操作在后端自动路由到 recommend 表，无需在用户表中创建数据
INSERT INTO navatation_user (user_id, username, password, email, avatar, role, status, deleted) VALUES
  ('u_admin_001', 'admin', '$2a$10$hXqs9VDJ2gtfpR7yiDNupOP.dkJ28ieTokvYZ8.Cb4rPAvYGEoFVi', 'admin@navatation.com', NULL, 'ADMIN', 1, 0);

SET FOREIGN_KEY_CHECKS=1;
