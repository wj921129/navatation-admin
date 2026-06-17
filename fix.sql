SET NAMES utf8mb4;
UPDATE navatation_recommend_todo_item SET content = '📝 欢迎使用极简导航页' WHERE row_id = 1;
UPDATE navatation_recommend_todo_item SET content = '💡 提示：点击右下角按钮注册/登录，可永久云端保存您的数据哦' WHERE row_id = 3;
UPDATE navatation_todo_item SET content = '📝 欢迎使用极简导航页' WHERE content LIKE '? 欢迎使用极简导航页';
UPDATE navatation_todo_item SET content = '💡 提示：点击右下角按钮注册/登录，可永久云端保存您的数据哦' WHERE content LIKE '? 提示：点击右下角按钮注册/登录%';
