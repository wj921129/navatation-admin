package com.navatation.business;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.navatation.business.dto.req.RegisterReqDTO;
import com.navatation.business.entity.nav.NavCategory;
import com.navatation.business.entity.nav.NavShortcut;
import com.navatation.business.entity.nav.UserWidget;
import com.navatation.business.entity.root.RootCategory;
import com.navatation.business.entity.root.RootConfig;
import com.navatation.business.entity.root.RootShortcut;
import com.navatation.business.entity.root.RootWidget;
import com.navatation.business.entity.user.User;
import com.navatation.business.entity.user.UserConfig;
import com.navatation.business.mapper.NavCategoryMapper;
import com.navatation.business.mapper.NavShortcutMapper;
import com.navatation.business.mapper.RootCategoryMapper;
import com.navatation.business.mapper.RootConfigMapper;
import com.navatation.business.mapper.RootShortcutMapper;
import com.navatation.business.mapper.RootWidgetMapper;
import com.navatation.business.mapper.UserConfigMapper;
import com.navatation.business.mapper.UserMapper;
import com.navatation.business.mapper.UserWidgetMapper;
import com.navatation.business.service.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Author admin
 * @CreateTime 2026-06-09
 * @Description AuthService 集成测试类，验证新注册用户默认同步拷贝管理员的设置、导航网址与小组件逻辑的正确性
 */
@SpringBootTest(classes = NavatationApplication.class)
public class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private com.navatation.business.mapper.RootUserMapper rootUserMapper;

    @Autowired
    private UserConfigMapper userConfigMapper;

    @Autowired
    private NavCategoryMapper navCategoryMapper;

    @Autowired
    private NavShortcutMapper navShortcutMapper;

    @Autowired
    private RootConfigMapper rootConfigMapper;

    @Autowired
    private RootCategoryMapper rootCategoryMapper;

    @Autowired
    private RootShortcutMapper rootShortcutMapper;

    @Autowired
    private RootWidgetMapper rootWidgetMapper;

    @Autowired
    private UserWidgetMapper userWidgetMapper;

    /**
     * 测试用户注册时，将管理员的配置、网址与小组件自动同步到新创建用户
     */
    @Test
    public void testRegisterAndSync() {
        // 1. 验证超级管理员账号 admin 是否存在于数据库中
        com.navatation.business.entity.root.RootUser admin = rootUserMapper.selectOne(
                new LambdaQueryWrapper<com.navatation.business.entity.root.RootUser>().last("LIMIT 1"));
        Assertions.assertNotNull(admin, "管理员账户应该存在于数据库中");
        String adminId = admin.getUserId();

        // 2. 模拟新用户注册请求
        String username = "st_" + (System.currentTimeMillis() % 100000000L);
        RegisterReqDTO req = new RegisterReqDTO();
        req.setUsername(username);
        req.setPassword("TestSyncPass123!");
        req.setConfirmPassword("TestSyncPass123!");
        
        // 执行注册与同步
        authService.register(req);

        // 3. 校验注册生成的新用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        Assertions.assertNotNull(user, "注册成功后用户应该在表中");
        String userId = user.getUserId();

        // 4. 验证 UserConfig 设置是否被完美拷贝
        RootConfig rootConfig = rootConfigMapper.selectOne(
                new LambdaQueryWrapper<RootConfig>().eq(RootConfig::getUserId, adminId));
        UserConfig userConfig = userConfigMapper.selectOne(
                new LambdaQueryWrapper<UserConfig>().eq(UserConfig::getUserId, userId));
        
        Assertions.assertNotNull(userConfig, "新用户的 UserConfig 配置记录应该被创建");
        if (rootConfig != null) {
            Assertions.assertEquals(rootConfig.getSearchEngine(), userConfig.getSearchEngine(), "搜索引擎设置拷贝不一致");
            Assertions.assertEquals(rootConfig.getBackgroundImage(), userConfig.getBackgroundImage(), "壁纸设置拷贝不一致");
            Assertions.assertEquals(rootConfig.getBackgroundType(), userConfig.getBackgroundType(), "壁纸类型拷贝不一致");
            Assertions.assertEquals(rootConfig.getSearchBoxWidth(), userConfig.getSearchBoxWidth(), "搜索框宽度拷贝不一致");
            Assertions.assertEquals(rootConfig.getTheme(), userConfig.getTheme(), "主题模式拷贝不一致");
        }

        // 5. 验证 NavCategory 分类 与 NavShortcut 快捷图标是否完美拷贝
        List<RootCategory> rootCategories = rootCategoryMapper.selectList(
                new LambdaQueryWrapper<RootCategory>().eq(RootCategory::getUserId, adminId));
        List<NavCategory> userCategories = navCategoryMapper.selectList(
                new LambdaQueryWrapper<NavCategory>().eq(NavCategory::getUserId, userId));

        if (rootCategories != null && !rootCategories.isEmpty()) {
            Assertions.assertEquals(rootCategories.size(), userCategories.size(), "新用户的导航分类数量应与管理员的一致");
            for (RootCategory adminCat : rootCategories) {
                // 在新用户分类中定位同名分类
                NavCategory userCat = userCategories.stream()
                        .filter(c -> c.getName().equals(adminCat.getName()))
                        .findFirst()
                        .orElse(null);
                Assertions.assertNotNull(userCat, "应该拷贝并存在分类: " + adminCat.getName());

                // 检验该分类下的快捷方式拷贝是否一致
                List<RootShortcut> adminShortcuts = rootShortcutMapper.selectList(
                        new LambdaQueryWrapper<RootShortcut>().eq(RootShortcut::getCategoryId, adminCat.getCategoryId()));
                List<NavShortcut> userShortcuts = navShortcutMapper.selectList(
                        new LambdaQueryWrapper<NavShortcut>().eq(NavShortcut::getCategoryId, userCat.getCategoryId()));
                
                Assertions.assertEquals(adminShortcuts.size(), userShortcuts.size(), "分类 [" + adminCat.getName() + "] 下快捷网址拷贝数量不一致");
                for (RootShortcut adminSc : adminShortcuts) {
                    NavShortcut userSc = userShortcuts.stream()
                            .filter(s -> s.getName().equals(adminSc.getName()))
                            .findFirst()
                            .orElse(null);
                    Assertions.assertNotNull(userSc, "应该存在快捷网址: " + adminSc.getName());
                    Assertions.assertEquals(adminSc.getUrl(), userSc.getUrl(), "网址链接不一致");
                    Assertions.assertEquals(adminSc.getIconType(), userSc.getIconType(), "图标类型不一致");
                    Assertions.assertEquals(adminSc.getIconValue(), userSc.getIconValue(), "图标值不一致");
                    Assertions.assertEquals(adminSc.getIconColor(), userSc.getIconColor(), "图标颜色不一致");
                }
            }
        } else {
            Assertions.assertEquals(1, userCategories.size(), "若管理员无分类，降级兜底应创建1个分类");
            Assertions.assertEquals("常用", userCategories.get(0).getName(), "降级兜底分类名称应为常用");
        }

        // 6. 验证 UserWidget 桌面上配置的小组件是否被成功同步
        List<RootWidget> rootWidgets = rootWidgetMapper.selectList(
                new LambdaQueryWrapper<RootWidget>().eq(RootWidget::getUserId, adminId));
        List<UserWidget> userWidgets = userWidgetMapper.selectList(
                new LambdaQueryWrapper<UserWidget>().eq(UserWidget::getUserId, userId));

        if (rootWidgets != null && !rootWidgets.isEmpty()) {
            Assertions.assertEquals(rootWidgets.size(), userWidgets.size(), "小组件拷贝数量不一致");
            for (RootWidget adminW : rootWidgets) {
                UserWidget userW = userWidgets.stream()
                        .filter(w -> w.getType().equals(adminW.getType()) && w.getStyle().equals(adminW.getStyle()))
                        .findFirst()
                        .orElse(null);
                Assertions.assertNotNull(userW, "应该存在相同类型和样式的小组件");
                Assertions.assertEquals(adminW.getX(), userW.getX(), "小组件坐标X轴不一致");
                Assertions.assertEquals(adminW.getY(), userW.getY(), "小组件坐标Y轴不一致");
                Assertions.assertEquals(adminW.getMeta(), userW.getMeta(), "小组件元数据不一致");
            }
        }
    }
}
