import re

with open('dml.sql', 'rb') as f:
    content = f.read().decode('utf-8', errors='ignore')

# 找到 INSERT INTO \
avatation_recommend_category\ VALUES 后面的一直到分号的地方替换掉
content = re.sub(
    r'INSERT INTO 
avatation_recommend_category VALUES .*?;',
    r"INSERT INTO 
avatation_recommend_category VALUES (1,'RC1','看视频',0,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(2,'RC2','AI工具',1,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(3,'RC3','Web开发',2,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(4,'RC4','购物',3,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(5,'RC5','新闻资讯',4,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(6,'RC6','游戏',5,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(7,'RC7','音乐',6,'2026-06-12 09:40:40','2026-06-12 09:40:40'),(8,'RC8','办公效率',7,'2026-06-12 09:40:40','2026-06-12 09:40:40');",
    content
)

with open('dml.sql', 'wb') as f:
    f.write(content.encode('utf-8'))
