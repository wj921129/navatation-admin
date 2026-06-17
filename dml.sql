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
TRUNCATE TABLE navatation_recommend_config;
TRUNCATE TABLE navatation_recommend_shortcut;
TRUNCATE TABLE navatation_recommend_todo_item;
TRUNCATE TABLE navatation_recommend_widget;
TRUNCATE TABLE navatation_recommend_home_shortcut;
TRUNCATE TABLE navatation_user;

-- ============================================
-- 2. 数据初始化
-- ============================================

INSERT INTO navatation_recommend_config (row_id, config_id, search_engine, background_image, background_type, search_box_width, search_box_height, search_box_margin_top, icon_size, icon_radius, icon_spacing_x, icon_spacing_y, icon_text_gap, text_size, icons_margin_top, icons_margin_x, theme, deleted, created_at, updated_at) VALUES
  (1,'RCG0000000000000000001','google','/uploads/sys_data/bg_img/wallpaper_16.jpg','URL',46,58,251,64,50,18,15,12,14,78,6,'system',0,'2026-06-16 22:13:19','2026-06-16 22:41:17');

INSERT INTO navatation_nav_category (row_id, category_id, user_id, name, sort_order, deleted, created_at, updated_at) VALUES
  (1,'RC1','u_admin_001','看视频',0.00,0,'2026-06-16 22:13:19','2026-06-16 22:13:19'),
  (2,'RC2','u_admin_001','AI工具',1.00,0,'2026-06-16 22:13:19','2026-06-16 22:13:19'),
  (3,'RC3','u_admin_001','Web开发',2.00,0,'2026-06-16 22:13:19','2026-06-16 22:13:19'),
  (4,'RC4','u_admin_001','购物',3.00,0,'2026-06-16 22:13:19','2026-06-16 22:13:19'),
  (5,'RC5','u_admin_001','新闻资讯',4.00,0,'2026-06-16 22:13:19','2026-06-16 22:13:19'),
  (6,'RC6','u_admin_001','游戏',5.00,0,'2026-06-16 22:13:19','2026-06-16 22:13:19'),
  (7,'RC7','u_admin_001','音乐',6.00,0,'2026-06-16 22:13:19','2026-06-16 22:13:19'),
  (8,'RC8','u_admin_001','办公效率',7.00,0,'2026-06-16 22:13:19','2026-06-16 22:13:19');

INSERT INTO navatation_recommend_shortcut (row_id, shortcut_id, category_id, name, url, icon_type, icon_value, icon_color, sort_order, deleted, created_at, updated_at) VALUES
  (1,'RS1','RC1','YouTube','https://youtube.com','FAVICON','/uploads/icon/sys/youtube.com_14dd5266.ico','#FF0000',7.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:38'),
  (2,'RS2','RC1','Netflix','https://netflix.com','FAVICON','/uploads/icon/sys/netflix.com_084b9a35.ico','#E50914',8.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:38'),
  (3,'RS3','RC1','Bilibili','https://bilibili.com','FAVICON','/uploads/icon/sys/bilibili.com_0575016f.png','#00A1D6',1.00,0,'2026-06-16 22:13:19','2026-06-16 22:41:19'),
  (4,'RS4','RC1','Twitch','https://twitch.tv','FAVICON','/uploads/icon/sys/twitch.tv_1da58962.png','#9146FF',6.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (5,'RS5','RC1','腾讯视频','https://v.qq.com','FAVICON','/uploads/icon/sys/v.qq.com_e96dd4d6.png','#FF8200',2.00,0,'2026-06-16 22:13:19','2026-06-16 22:41:19'),
  (6,'RS6','RC1','爱奇艺','https://iqiyi.com','FAVICON','/uploads/icon/sys/iqiyi.com_7ef31f5c.ico','#00CC00',3.00,0,'2026-06-16 22:13:19','2026-06-16 22:31:41'),
  (7,'RS7','RC1','优酷','https://youku.com','FAVICON','/uploads/icon/sys/youku.com_2e112618.png','#1A90FF',4.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:46'),
  (8,'RS8','RC1','抖音','https://douyin.com','FAVICON','/uploads/icon/sys/douyin.com_8c3eea2a.ico','#111111',5.00,0,'2026-06-16 22:13:19','2026-06-16 22:34:48'),
  (9,'RS9','RC2','ChatGPT','https://chat.openai.com','FAVICON','/uploads/icon/sys/chat.openai.com_234455f5.ico','#10A37F',7.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:38'),
  (10,'RS10','RC2','Claude','https://claude.ai','FAVICON','/uploads/icon/sys/claude.ai_9e2ae480.ico','#CC9B7A',8.00,0,'2026-06-16 22:13:19','2026-06-16 22:34:48'),
  (11,'RS11','RC2','DeepSeek','https://chat.deepseek.com','FAVICON','/uploads/icon/sys/chat.deepseek.com_29b6efe8.png','#1254FF',3.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (12,'RS12','RC2','Gemini','https://gemini.google.com','FAVICON','/uploads/icon/sys/gemini.google.com_7509d427.svg','#4285F4',9.00,0,'2026-06-16 22:13:19','2026-06-16 22:34:48'),
  (13,'RS13','RC2','豆包','https://doubao.com','FAVICON','/uploads/icon/sys/doubao.com_e0d1b3ee.png','#0057FF',1.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:40'),
  (14,'RS14','RC2','Kimi','https://kimi.moonshot.cn','FAVICON','/uploads/icon/sys/kimi.moonshot.cn_e05bdbbe.ico','#5C5CFF',5.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:40'),
  (15,'RS15','RC2','文心一言','https://yiyan.baidu.com','FAVICON','/uploads/icon/sys/yiyan.baidu.com_b5170c15.ico','#2932E1',4.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:40'),
  (16,'RS16','RC2','智谱清言','https://chatglm.cn','FAVICON','/uploads/icon/sys/chatglm.cn_0d78f74d.ico','#3D52F5',6.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:40'),
  (17,'RS17','RC3','GitHub','https://github.com','FAVICON','/uploads/icon/sys/github.com_99cd2175.svg','#181717',1.00,0,'2026-06-16 22:13:19','2026-06-16 22:41:19'),
  (18,'RS18','RC3','Stack Overflow','https://stackoverflow.com','FAVICON','/uploads/icon/sys/stackoverflow.com_d0cc85b2.ico','#F58025',8.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (19,'RS19','RC3','MDN','https://developer.mozilla.org','FAVICON','/uploads/icon/sys/developer.mozilla.org_22a6b05b.ico','#000000',7.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (20,'RS20','RC3','Gitee','https://gitee.com','FAVICON','/uploads/icon/sys/gitee.com_a24f2d4e.ico','#C71D23',2.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (21,'RS21','RC3','掘金','https://juejin.cn','FAVICON','/uploads/icon/sys/juejin.cn_78a9c4dc.png','#1E80FF',4.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:40'),
  (22,'RS22','RC3','CSDN','https://csdn.net','FAVICON','/uploads/icon/sys/csdn.net_5e937446.ico','#E2231A',3.00,0,'2026-06-16 22:13:19','2026-06-16 22:41:19'),
  (23,'RS23','RC3','阿里云','https://aliyun.com','FAVICON','/uploads/icon/sys/aliyun.com_a667fa51.ico','#FF6A00',5.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:40'),
  (24,'RS24','RC3','腾讯云','https://cloud.tencent.com','FAVICON','/uploads/icon/sys/cloud.tencent.com_74c4c45d.ico','#00A4FF',6.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:40'),
  (25,'RS25','RC4','淘宝','https://taobao.com','FAVICON','/uploads/icon/sys/taobao.com_e08d2529.png','#FF6A00',1.00,0,'2026-06-16 22:13:19','2026-06-16 22:41:19'),
  (26,'RS26','RC4','京东','https://jd.com','FAVICON','/uploads/icon/sys/jd.com_6485ce0e.ico','#E3393C',2.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:38'),
  (27,'RS27','RC4','拼多多','https://pinduoduo.com','FAVICON','/uploads/icon/sys/pinduoduo.com_8ae75de8.png','#E02E24',3.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (28,'RS28','RC4','唯品会','https://vip.com','FAVICON','/uploads/icon/sys/vip.com_c4c7f177.ico','#F10180',4.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (29,'RS29','RC4','美团','https://meituan.com','FAVICON','/uploads/icon/sys/meituan.com_85ff8e1f.ico','#FFC300',5.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (30,'RS30','RC4','Amazon','https://amazon.com','FAVICON','/uploads/icon/sys/amazon.com_a2a82838.ico','#FF9900',6.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (31,'RS31','RC4','eBay','https://ebay.com','FAVICON','/uploads/icon/sys/ebay.com_12c8e292.ico','#E53238',7.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (32,'RS32','RC4','AliExpress','https://aliexpress.com','FAVICON','/uploads/icon/sys/aliexpress.com_1e6e512d.ico','#E62E04',8.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (33,'RS33','RC5','知乎','https://zhihu.com','FAVICON','/uploads/icon/sys/zhihu.com_227e8153.png','#0084FF',1.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:38'),
  (34,'RS34','RC5','微博','https://weibo.com','FAVICON','/uploads/icon/sys/weibo.com_cfaa286e.ico','#E6162D',2.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (35,'RS35','RC5','今日头条','https://toutiao.com','FAVICON','/uploads/icon/sys/toutiao.com_cbe956ef.ico','#F85959',3.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (36,'RS36','RC5','澎湃新闻','https://thepaper.cn','FAVICON','/uploads/icon/sys/thepaper.cn_4e9334ec.ico','#00AEB5',4.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (37,'RS37','RC5','腾讯新闻','https://news.qq.com','FAVICON','/uploads/icon/sys/news.qq.com_3043163b.ico','#1E80FF',5.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (38,'RS38','RC5','Reddit','https://reddit.com','FAVICON','/uploads/icon/sys/reddit.com_1fd7de7d.ico','#FF4500',6.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (39,'RS39','RC5','BBC','https://bbc.com','FAVICON','/uploads/icon/sys/bbc.com_9373780e.ico','#B71C1C',7.00,0,'2026-06-16 22:13:19','2026-06-16 22:34:48'),
  (40,'RS40','RC5','Medium','https://medium.com','FAVICON','/uploads/icon/sys/medium.com_d03cf468.ico','#000000',8.00,0,'2026-06-16 22:13:19','2026-06-16 22:34:48'),
  (41,'RS41','RC6','Steam','https://store.steampowered.com','FAVICON','/uploads/icon/sys/store.steampowered.com_afe840c0.ico','#171A21',1.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:38'),
  (42,'RS42','RC6','Epic Games','https://epicgames.com','FAVICON','/uploads/icon/sys/epicgames.com_2526388c.ico','#313131',2.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (43,'RS43','RC6','TapTap','https://taptap.cn','FAVICON','/uploads/icon/sys/taptap.cn_a9116d9f.ico','#00D1A1',3.00,0,'2026-06-16 22:13:19','2026-06-16 22:34:48'),
  (44,'RS44','RC6','4399','https://4399.com','FAVICON','/uploads/icon/sys/4399.com_a4e988bb.ico','#FF7700',4.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:40'),
  (45,'RS45','RC6','NGA','https://nga.cn','FAVICON','/uploads/icon/sys/nga.cn_c555dff1.ico','#7E1111',5.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:40'),
  (46,'RS46','RC6','游民星空','https://gamersky.com','FAVICON','/uploads/icon/sys/gamersky.com_4465ccf5.ico','#1C7430',6.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:40'),
  (47,'RS47','RC6','3DM','https://3dmgame.com','FAVICON','/uploads/icon/sys/3dmgame.com_28f3e1e2.ico','#FF2626',7.00,0,'2026-06-16 22:13:19','2026-06-16 22:34:48'),
  (48,'RS48','RC6','Discord','https://discord.com','FAVICON','/uploads/icon/sys/discord.com_1f91556f.png','#5865F2',8.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:40'),
  (49,'RS49','RC7','网易云音乐','https://music.163.com','FAVICON','/uploads/icon/sys/music.163.com_a52268be.ico','#E60026',1.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:38'),
  (50,'RS50','RC7','QQ音乐','https://y.qq.com','FAVICON','/uploads/icon/sys/y.qq.com_4a73e2a8.ico','#2CAF6F',2.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:38'),
  (51,'RS51','RC7','酷狗音乐','https://kugou.com','FAVICON','/uploads/icon/sys/kugou.com_ab5e0948.ico','#00A9FF',3.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:38'),
  (52,'RS52','RC7','咪咕音乐','https://music.migu.cn','FAVICON','/uploads/icon/sys/music.migu.cn_563a32f9.ico','#FF007F',5.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:38'),
  (53,'RS53','RC7','Spotify','https://spotify.com','FAVICON','/uploads/icon/sys/spotify.com_b61b046f.ico','#1DB954',6.00,0,'2026-06-16 22:13:19','2026-06-16 22:34:48'),
  (54,'RS54','RC7','Apple Music','https://music.apple.com','FAVICON','/uploads/icon/sys/music.apple.com_8d49e3cb.png','#FA243C',7.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (55,'RS55','RC7','SoundCloud','https://soundcloud.com','FAVICON','/uploads/icon/sys/soundcloud.com_9aba6816.ico','#FF5500',8.00,0,'2026-06-16 22:13:19','2026-06-16 22:34:48'),
  (56,'RS56','RC7','YouTube Music','https://music.youtube.com','FAVICON','/uploads/icon/sys/music.youtube.com_c791cd08.ico','#FF0000',9.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (57,'RS57','RC8','飞书','https://feishu.cn','FAVICON','/uploads/icon/sys/feishu.cn_487c5fcf.png','#00D1A1',1.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:38'),
  (58,'RS58','RC8','钉钉','https://dingtalk.com','FAVICON','/uploads/icon/sys/dingtalk.com_da87408e.ico','#0089FF',2.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:38'),
  (59,'RS59','RC8','语雀','https://yuque.com','FAVICON','/uploads/icon/sys/yuque.com_e8b29809.png','#00B96B',3.00,0,'2026-06-16 22:13:19','2026-06-16 22:27:20'),
  (60,'RS60','RC8','腾讯文档','https://docs.qq.com','FAVICON','/uploads/icon/sys/docs.qq.com_f181392d.ico','#007BFF',4.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:38'),
  (61,'RS61','RC8','WPS','https://wps.cn','FAVICON','/uploads/icon/sys/wps.cn_9c7410a0.png','#D9383A',5.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (62,'RS62','RC8','Notion','https://notion.so','FAVICON','/uploads/icon/sys/notion.so_868e608a.ico','#000000',6.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (63,'RS63','RC8','Figma','https://figma.com','FAVICON','/uploads/icon/sys/figma.com_e7482091.ico','#F24E1E',7.00,0,'2026-06-16 22:13:19','2026-06-16 22:27:20'),
  (64,'RS64','RC8','Slack','https://slack.com','FAVICON','/uploads/icon/sys/slack.com_1df9e38b.png','#4A154B',9.00,0,'2026-06-16 22:13:19','2026-06-16 22:22:39'),
  (65,'RS5721897925679009846773','RC2','千问','https://www.qianwen.com/','FAVICON','/uploads/icon/sys/www.qianwen.com_9e9dbd93.png','#4285F4',2.00,0,'2026-06-16 22:28:29','2026-06-16 22:33:48'),
  (66,'RS0829762616026833556613','RC7','酷我音乐','https://bd.kuwo.cn/','FAVICON','/uploads/icon/sys/bd.kuwo.cn_9c757e70.png','#4285F4',4.00,0,'2026-06-16 22:29:43','2026-06-16 22:30:21'),
  (67,'RS9996827573144330336308','RC8','Stich','https://stitch.withgoogle.com/','FAVICON','/uploads/icon/sys/stitch.withgoogle.com_4b77bef0.jpg','#4285F4',8.00,0,'2026-06-16 22:30:21','2026-06-16 22:31:55');

INSERT INTO navatation_recommend_todo_item (row_id, todo_id, content, sort_order, deleted, created_at, updated_at) VALUES
  (1,'TD0000000000000000000001','👋 欢迎使用极简导航页',1.00,0,'2026-06-16 22:13:19','2026-06-16 22:13:19'),
  (2,'TD0000000000000000000002','✅ 点击完成待办事项',2.00,0,'2026-06-16 22:13:19','2026-06-16 22:13:19'),
  (3,'TD0000000000000000000003','💡 提示：点击右下角按钮注册/登录，可永久云端保存您的数据哦',3.00,0,'2026-06-16 22:13:19','2026-06-16 22:13:19');

INSERT INTO navatation_recommend_widget (row_id, widget_id, type, style, x, y, meta, deleted, created_at, updated_at) VALUES
  (10,'WG0000000000000000000001','clock','flip',2.08,4.23,'{}',0,'2026-06-16 22:41:10','2026-06-16 22:41:10'),
  (11,'WG0000000000000000000002','calendar','month',2.08,19.05,'{}',0,'2026-06-16 22:41:10','2026-06-16 22:41:10'),
  (12,'WG0000000000000000000003','weather','simple',14.58,4.23,'{\"locations\": [{\"lat\": 30.58333, \"lon\": 114.26667, \"name\": \"武汉, 湖北, 中国\"}]}',0,'2026-06-16 22:41:10','2026-06-16 22:41:10');

INSERT INTO navatation_recommend_home_shortcut (row_id, shortcut_id, name, url, icon_type, icon_value, icon_color, sort_order, created_at, updated_at) VALUES
  (5,'SC8630086175596035878161','Bilibili','https://bilibili.com','FAVICON','/uploads/icon/sys/bilibili.com_0575016f.png','#00A1D6',0.00,'2026-06-16 22:33:54','2026-06-16 22:41:19'),
  (6,'SC8112010883337743017806','腾讯视频','https://v.qq.com','FAVICON','/uploads/icon/sys/v.qq.com_e96dd4d6.png','#FF8200',0.00,'2026-06-16 22:33:54','2026-06-16 22:41:19'),
  (8,'SC7561714286274385768443','豆包','https://doubao.com','FAVICON','/uploads/icon/sys/doubao.com_e0d1b3ee.png','#0057FF',0.00,'2026-06-16 22:33:54','2026-06-16 22:33:54'),
  (9,'SC7963737778073185806533','千问','https://www.qianwen.com/','FAVICON','https://icons.duckduckgo.com/ip3/www.qianwen.com.ico','#4285F4',0.00,'2026-06-16 22:33:54','2026-06-16 22:33:54'),
  (11,'SC6614435730818942928417','GitHub','https://github.com','FAVICON','/uploads/icon/sys/github.com_99cd2175.svg','#181717',0.00,'2026-06-16 22:33:54','2026-06-16 22:41:19'),
  (13,'SC5939726483872721111687','CSDN','https://csdn.net','FAVICON','/uploads/icon/sys/csdn.net_5e937446.ico','#E2231A',0.00,'2026-06-16 22:33:54','2026-06-16 22:41:19'),
  (14,'SC5761128712573541734816','淘宝','https://taobao.com','FAVICON','/uploads/icon/sys/taobao.com_e08d2529.png','#FF6A00',0.00,'2026-06-16 22:33:54','2026-06-16 22:41:19'),
  (15,'SC5560565479059826031208','京东','https://jd.com','FAVICON','/uploads/icon/sys/jd.com_6485ce0e.ico','#E3393C',0.00,'2026-06-16 22:33:54','2026-06-16 22:33:54'),
  (16,'SC3274933582015946895246','知乎','https://zhihu.com','FAVICON','/uploads/icon/sys/zhihu.com_227e8153.png','#0084FF',0.00,'2026-06-16 22:33:54','2026-06-16 22:33:54'),
  (17,'SC4058628313261628503325','微博','https://weibo.com','FAVICON','/uploads/icon/sys/weibo.com_cfaa286e.ico','#E6162D',0.00,'2026-06-16 22:33:54','2026-06-16 22:33:54'),
  (18,'SC5221617871445761267307','Steam','https://store.steampowered.com','FAVICON','/uploads/icon/sys/store.steampowered.com_afe840c0.ico','#171A21',0.00,'2026-06-16 22:33:54','2026-06-16 22:33:54'),
  (19,'SC7559916969134400816867','网易云音乐','https://music.163.com','FAVICON','/uploads/icon/sys/music.163.com_a52268be.ico','#E60026',0.00,'2026-06-16 22:39:32','2026-06-16 22:39:32'),
  (20,'SC1234143032453173141154','酷狗音乐','https://kugou.com','FAVICON','/uploads/icon/sys/kugou.com_ab5e0948.ico','#00A9FF',0.00,'2026-06-16 22:39:32','2026-06-16 22:39:32'),
  (21,'SC9109376736806267677906','飞书','https://feishu.cn','FAVICON','/uploads/icon/sys/feishu.cn_487c5fcf.png','#00D1A1',0.00,'2026-06-16 22:39:32','2026-06-16 22:39:32'),
  (22,'SC1039419980713381820799','腾讯文档','https://docs.qq.com','FAVICON','/uploads/icon/sys/docs.qq.com_f181392d.ico','#007BFF',0.00,'2026-06-16 22:39:32','2026-06-16 22:39:32');

INSERT INTO navatation_user (row_id, user_id, username, password, email, avatar, role, status, last_login_at, last_login_ip, deleted, created_at, updated_at) VALUES
  (1,'u_admin_001','admin','$2a$10$hXqs9VDJ2gtfpR7yiDNupOP.dkJ28ieTokvYZ8.Cb4rPAvYGEoFVi','admin@navatation.com',NULL,'ADMIN',1,'2026-06-16 22:14:22',NULL,0,'2026-06-16 22:13:19','2026-06-16 22:13:19');

SET FOREIGN_KEY_CHECKS=1;
