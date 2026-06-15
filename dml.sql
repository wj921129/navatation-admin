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
-- Dumping data for table `navatation_nav_category`
--

LOCK TABLES `navatation_nav_category` WRITE;
/*!40000 ALTER TABLE `navatation_nav_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `navatation_nav_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `navatation_nav_shortcut`
--

LOCK TABLES `navatation_nav_shortcut` WRITE;
/*!40000 ALTER TABLE `navatation_nav_shortcut` DISABLE KEYS */;
/*!40000 ALTER TABLE `navatation_nav_shortcut` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `navatation_recommend_category`
--

LOCK TABLES `navatation_recommend_category` WRITE;
/*!40000 ALTER TABLE `navatation_recommend_category` DISABLE KEYS */;
INSERT INTO `navatation_recommend_category` VALUES (1,'RC1','看视频',0,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(2,'RC2','AI工具',1,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(3,'RC3','Web开发',2,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(4,'RC4','购物',3,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(5,'RC5','新闻资讯',4,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(6,'RC6','游戏',5,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(7,'RC7','音乐',6,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(8,'RC8','办公效率',7,'2026-06-12 09:40:40','2026-06-12 09:40:40');
/*!40000 ALTER TABLE `navatation_recommend_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `navatation_recommend_config`
--

LOCK TABLES `navatation_recommend_config` WRITE;
/*!40000 ALTER TABLE `navatation_recommend_config` DISABLE KEYS */;
INSERT INTO `navatation_recommend_config` VALUES (1,'RCG0000000000000000001','google','https://images.unsplash.com/photo-1598439473183-42c9301db5dc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=2400','URL',50,64,192,64,50,32,48,12,14,64,10,'light','2026-06-12 09:40:40','2026-06-12 09:40:40');
/*!40000 ALTER TABLE `navatation_recommend_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `navatation_recommend_shortcut`
--

LOCK TABLES `navatation_recommend_shortcut` WRITE;
/*!40000 ALTER TABLE `navatation_recommend_shortcut` DISABLE KEYS */;
INSERT INTO `navatation_recommend_shortcut` VALUES (1,'RS1','RC1','YouTube','https://youtube.com','FAVICON','https://icons.duckduckgo.com/ip3/youtube.com.ico','#FF0000',1,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(2,'RS2','RC1','Netflix','https://netflix.com','FAVICON','https://icons.duckduckgo.com/ip3/netflix.com.ico','#E50914',2,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(3,'RS3','RC1','Bilibili','https://bilibili.com','FAVICON','https://icons.duckduckgo.com/ip3/bilibili.com.ico','#00A1D6',3,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(4,'RS4','RC1','Twitch','https://twitch.tv','FAVICON','https://icons.duckduckgo.com/ip3/twitch.tv.ico','#9146FF',4,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(5,'RS5','RC1','腾讯视频','https://v.qq.com','FAVICON','https://icons.duckduckgo.com/ip3/v.qq.com.ico','#FF8200',5,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(6,'RS6','RC1','爱奇艺','https://iqiyi.com','FAVICON','https://icons.duckduckgo.com/ip3/iqiyi.com.ico','#00CC00',6,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(7,'RS7','RC1','优酷','https://youku.com','FAVICON','https://www.google.com/s2/favicons?sz=64&domain=youku.com','#1A90FF',7,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(8,'RS8','RC1','抖音','https://douyin.com','FAVICON','https://www.google.com/s2/favicons?sz=64&domain=douyin.com','#111111',8,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(9,'RS9','RC2','ChatGPT','https://chat.openai.com','FAVICON','https://icons.duckduckgo.com/ip3/chat.openai.com.ico','#10A37F',1,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(10,'RS10','RC2','Claude','https://claude.ai','FAVICON','https://icons.duckduckgo.com/ip3/claude.ai.ico','#CC9B7A',2,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(11,'RS11','RC2','DeepSeek','https://chat.deepseek.com','FAVICON','https://www.google.com/s2/favicons?sz=64&domain=chat.deepseek.com','#1254FF',3,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(12,'RS12','RC2','Gemini','https://gemini.google.com','FAVICON','https://icons.duckduckgo.com/ip3/gemini.google.com.ico','#4285F4',4,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(13,'RS13','RC2','豆包','https://doubao.com','FAVICON','https://icons.duckduckgo.com/ip3/doubao.com.ico','#0057FF',5,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(14,'RS14','RC2','Kimi','https://kimi.moonshot.cn','FAVICON','https://icons.duckduckgo.com/ip3/kimi.moonshot.cn.ico','#5C5CFF',6,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(15,'RS15','RC2','文心一言','https://yiyan.baidu.com','FAVICON','https://icons.duckduckgo.com/ip3/yiyan.baidu.com.ico','#2932E1',7,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(16,'RS16','RC2','智谱清言','https://chatglm.cn','FAVICON','https://icons.duckduckgo.com/ip3/chatglm.cn.ico','#3D52F5',8,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(17,'RS17','RC3','GitHub','https://github.com','FAVICON','https://icons.duckduckgo.com/ip3/github.com.ico','#181717',1,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(18,'RS18','RC3','Stack Overflow','https://stackoverflow.com','FAVICON','https://icons.duckduckgo.com/ip3/stackoverflow.com.ico','#F58025',2,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(19,'RS19','RC3','MDN','https://developer.mozilla.org','FAVICON','https://icons.duckduckgo.com/ip3/developer.mozilla.org.ico','#000000',3,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(20,'RS20','RC3','Gitee','https://gitee.com','FAVICON','https://icons.duckduckgo.com/ip3/gitee.com.ico','#C71D23',4,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(21,'RS21','RC3','掘金','https://juejin.cn','FAVICON','https://www.google.com/s2/favicons?sz=64&domain=juejin.cn','#1E80FF',5,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(22,'RS22','RC3','CSDN','https://csdn.net','FAVICON','https://www.google.com/s2/favicons?sz=64&domain=csdn.net','#E2231A',6,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(23,'RS23','RC3','阿里云','https://aliyun.com','FAVICON','https://icons.duckduckgo.com/ip3/aliyun.com.ico','#FF6A00',7,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(24,'RS24','RC3','腾讯云','https://cloud.tencent.com','FAVICON','https://icons.duckduckgo.com/ip3/cloud.tencent.com.ico','#00A4FF',8,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(25,'RS25','RC4','淘宝','https://taobao.com','FAVICON','https://icons.duckduckgo.com/ip3/taobao.com.ico','#FF6A00',1,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(26,'RS26','RC4','京东','https://jd.com','FAVICON','https://icons.duckduckgo.com/ip3/jd.com.ico','#E3393C',2,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(27,'RS27','RC4','拼多多','https://pinduoduo.com','FAVICON','https://icons.duckduckgo.com/ip3/pinduoduo.com.ico','#E02E24',3,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(28,'RS28','RC4','唯品会','https://vip.com','FAVICON','https://icons.duckduckgo.com/ip3/vip.com.ico','#F10180',4,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(29,'RS29','RC4','美团','https://meituan.com','FAVICON','https://icons.duckduckgo.com/ip3/meituan.com.ico','#FFC300',5,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(30,'RS30','RC4','Amazon','https://amazon.com','FAVICON','https://icons.duckduckgo.com/ip3/amazon.com.ico','#FF9900',6,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(31,'RS31','RC4','eBay','https://ebay.com','FAVICON','https://icons.duckduckgo.com/ip3/ebay.com.ico','#E53238',7,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(32,'RS32','RC4','AliExpress','https://aliexpress.com','FAVICON','https://icons.duckduckgo.com/ip3/aliexpress.com.ico','#E62E04',8,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(33,'RS33','RC5','知乎','https://zhihu.com','FAVICON','https://www.google.com/s2/favicons?sz=64&domain=zhihu.com','#0084FF',1,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(34,'RS34','RC5','微博','https://weibo.com','FAVICON','https://icons.duckduckgo.com/ip3/weibo.com.ico','#E6162D',2,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(35,'RS35','RC5','今日头条','https://toutiao.com','FAVICON','https://icons.duckduckgo.com/ip3/toutiao.com.ico','#F85959',3,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(36,'RS36','RC5','澎湃新闻','https://thepaper.cn','FAVICON','https://icons.duckduckgo.com/ip3/thepaper.cn.ico','#00AEB5',4,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(37,'RS37','RC5','腾讯新闻','https://news.qq.com','FAVICON','https://icons.duckduckgo.com/ip3/news.qq.com.ico','#1E80FF',5,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(38,'RS38','RC5','Reddit','https://reddit.com','FAVICON','https://icons.duckduckgo.com/ip3/reddit.com.ico','#FF4500',6,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(39,'RS39','RC5','BBC','https://bbc.com','FAVICON','https://icons.duckduckgo.com/ip3/bbc.com.ico','#B71C1C',7,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(40,'RS40','RC5','Medium','https://medium.com','FAVICON','https://www.google.com/s2/favicons?sz=64&domain=medium.com','#000000',8,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(41,'RS41','RC6','Steam','https://store.steampowered.com','FAVICON','https://icons.duckduckgo.com/ip3/store.steampowered.com.ico','#171A21',1,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(42,'RS42','RC6','Epic Games','https://epicgames.com','FAVICON','https://icons.duckduckgo.com/ip3/epicgames.com.ico','#313131',2,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(43,'RS43','RC6','TapTap','https://taptap.cn','FAVICON','https://www.google.com/s2/favicons?sz=64&domain=taptap.cn','#00D1A1',3,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(44,'RS44','RC6','4399','https://4399.com','FAVICON','https://icons.duckduckgo.com/ip3/4399.com.ico','#FF7700',4,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(45,'RS45','RC6','NGA','https://nga.cn','FAVICON','https://icons.duckduckgo.com/ip3/nga.cn.ico','#7E1111',5,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(46,'RS46','RC6','游民星空','https://gamersky.com','FAVICON','https://icons.duckduckgo.com/ip3/gamersky.com.ico','#1C7430',6,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(47,'RS47','RC6','3DM','https://3dmgame.com','FAVICON','https://www.google.com/s2/favicons?sz=64&domain=3dmgame.com','#FF2626',7,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(48,'RS48','RC6','Discord','https://discord.com','FAVICON','https://icons.duckduckgo.com/ip3/discord.com.ico','#5865F2',8,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(49,'RS49','RC7','网易云音乐','https://music.163.com','FAVICON','https://icons.duckduckgo.com/ip3/music.163.com.ico','#E60026',1,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(50,'RS50','RC7','QQ音乐','https://y.qq.com','FAVICON','https://icons.duckduckgo.com/ip3/y.qq.com.ico','#2CAF6F',2,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(51,'RS51','RC7','酷狗音乐','https://kugou.com','FAVICON','https://icons.duckduckgo.com/ip3/kugou.com.ico','#00A9FF',3,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(52,'RS52','RC7','咪咕音乐','https://music.migu.cn','FAVICON','https://icons.duckduckgo.com/ip3/music.migu.cn.ico','#FF007F',4,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(53,'RS53','RC7','Spotify','https://spotify.com','FAVICON','https://icons.duckduckgo.com/ip3/spotify.com.ico','#1DB954',5,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(54,'RS54','RC7','Apple Music','https://music.apple.com','FAVICON','https://icons.duckduckgo.com/ip3/music.apple.com.ico','#FA243C',6,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(55,'RS55','RC7','SoundCloud','https://soundcloud.com','FAVICON','https://icons.duckduckgo.com/ip3/soundcloud.com.ico','#FF5500',7,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(56,'RS56','RC7','YouTube Music','https://music.youtube.com','FAVICON','https://icons.duckduckgo.com/ip3/music.youtube.com.ico','#FF0000',8,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(57,'RS57','RC8','飞书','https://feishu.cn','FAVICON','https://icons.duckduckgo.com/ip3/feishu.cn.ico','#00D1A1',1,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(58,'RS58','RC8','钉钉','https://dingtalk.com','FAVICON','https://icons.duckduckgo.com/ip3/dingtalk.com.ico','#0089FF',2,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(59,'RS59','RC8','语雀','https://yuque.com','FAVICON','https://icons.duckduckgo.com/ip3/yuque.com.ico','#00B96B',3,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(60,'RS60','RC8','腾讯文档','https://docs.qq.com','FAVICON','https://icons.duckduckgo.com/ip3/docs.qq.com.ico','#007BFF',4,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(61,'RS61','RC8','WPS','https://wps.cn','FAVICON','https://icons.duckduckgo.com/ip3/wps.cn.ico','#D9383A',5,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(62,'RS62','RC8','Notion','https://notion.so','FAVICON','https://icons.duckduckgo.com/ip3/notion.so.ico','#000000',6,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(63,'RS63','RC8','Figma','https://figma.com','FAVICON','https://icons.duckduckgo.com/ip3/figma.com.ico','#F24E1E',7,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(64,'RS64','RC8','Slack','https://slack.com','FAVICON','https://icons.duckduckgo.com/ip3/slack.com.ico','#4A154B',8,'2026-06-12 09:40:40','2026-06-12 09:40:40');
/*!40000 ALTER TABLE `navatation_recommend_shortcut` ENABLE KEYS */;
UNLOCK TABLES;

--


--


--


--
-- Dumping data for table `navatation_recommend_todo_item`
--

LOCK TABLES `navatation_recommend_todo_item` WRITE;
/*!40000 ALTER TABLE `navatation_recommend_todo_item` DISABLE KEYS */;
INSERT INTO `navatation_recommend_todo_item` VALUES (1,'TD0000000000000000000001','👋 欢迎使用极简导航页',0,1,NULL,'2026-06-12 09:43:41','2026-06-12 14:25:27'),(2,'TD0000000000000000000002','✅ 点击完成待办事项',0,2,NULL,'2026-06-12 09:43:50','2026-06-12 14:25:27'),(3,'TD0000000000000000000003','💡 提示：点击右下角按钮注册/登录，可永久云端保存您的数据！',0,3,NULL,'2026-06-12 09:43:50','2026-06-12 14:25:27');
/*!40000 ALTER TABLE `navatation_recommend_todo_item` ENABLE KEYS */;
UNLOCK TABLES;

--


--
-- Dumping data for table `navatation_recommend_widget`
--

LOCK TABLES `navatation_recommend_widget` WRITE;
/*!40000 ALTER TABLE `navatation_recommend_widget` DISABLE KEYS */;
INSERT INTO `navatation_recommend_widget` VALUES (1,'WG0000000000000000000001','clock','flip',2.08,4.23,'{}','2026-06-12 13:39:05','2026-06-12 14:26:04'),(2,'WG0000000000000000000002','calendar','month',2.08,19.05,'{}','2026-06-12 13:39:05','2026-06-12 14:26:04'),(3,'WG0000000000000000000003','weather','simple',14.58,4.23,'{\"locations\":[{\"name\":\"武汉, 湖北, 中国\",\"lat\":30.58333,\"lon\":114.26667}]}','2026-06-12 13:39:05','2026-06-12 14:26:04');
/*!40000 ALTER TABLE `navatation_recommend_widget` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `navatation_todo_item`
--

LOCK TABLES `navatation_todo_item` WRITE;
/*!40000 ALTER TABLE `navatation_todo_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `navatation_todo_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `navatation_user`
--

LOCK TABLES `navatation_user` WRITE;
/*!40000 ALTER TABLE `navatation_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `navatation_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `navatation_user_config`
--

LOCK TABLES `navatation_user_config` WRITE;
/*!40000 ALTER TABLE `navatation_user_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `navatation_user_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `navatation_user_widget`
--

LOCK TABLES `navatation_user_widget` WRITE;
/*!40000 ALTER TABLE `navatation_user_widget` DISABLE KEYS */;
/*!40000 ALTER TABLE `navatation_user_widget` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-12 14:27:20
