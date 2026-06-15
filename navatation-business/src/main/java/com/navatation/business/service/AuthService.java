package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.navatation.business.constant.SettingsConstants;
import com.navatation.business.dto.req.auth.ChangePasswordReqDTO;
import com.navatation.business.dto.req.auth.LoginReqDTO;
import com.navatation.business.dto.req.auth.RefreshTokenReqDTO;
import com.navatation.business.dto.req.auth.RegisterReqDTO;
import com.navatation.business.dto.resp.auth.LoginRespDTO;
import com.navatation.business.dto.resp.user.UserRespDTO;
import com.navatation.business.entity.nav.NavCategory;
import com.navatation.business.entity.nav.NavShortcut;
import com.navatation.business.entity.nav.TodoItem;
import com.navatation.business.entity.nav.UserWidget;
import com.navatation.business.entity.recommend.RecommendCategory;
import com.navatation.business.entity.recommend.RecommendConfig;
import com.navatation.business.entity.recommend.RecommendShortcut;
import com.navatation.business.entity.recommend.RecommendTodoItem;
import com.navatation.business.entity.recommend.RecommendWidget;
import com.navatation.business.entity.user.User;
import com.navatation.business.entity.user.UserConfig;
import com.navatation.business.mapper.NavCategoryMapper;
import com.navatation.business.mapper.NavShortcutMapper;
import com.navatation.business.mapper.RecommendCategoryMapper;
import com.navatation.business.mapper.RecommendConfigMapper;
import com.navatation.business.mapper.RecommendShortcutMapper;
import com.navatation.business.mapper.RecommendTodoItemMapper;
import com.navatation.business.mapper.RecommendWidgetMapper;
import com.navatation.business.mapper.TodoItemMapper;
import com.navatation.business.mapper.UserConfigMapper;
import com.navatation.business.mapper.UserMapper;
import com.navatation.business.mapper.UserWidgetMapper;
import com.navatation.common.BizException;
import com.navatation.common.IdUtils;
import com.navatation.common.RedisConstants;
import com.navatation.common.ResultCode;
import com.navatation.framework.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author admin
 * @CreateTime 2026-05-15
 * @Description 认证服务，处理用户注册、登录、登出、Token刷新等核心认证逻辑
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final int USER_STATUS_DISABLED = 0;
    private static final int USER_STATUS_ENABLED = 1;

    private final UserMapper userMapper;
    private final UserConfigMapper userConfigMapper;
    private final NavCategoryMapper navCategoryMapper;
    private final NavShortcutMapper navShortcutMapper;
    private final UserWidgetMapper userWidgetMapper;
    private final TodoItemMapper todoItemMapper;
    
    private final RecommendConfigMapper recommendConfigMapper;
    private final RecommendCategoryMapper recommendCategoryMapper;
    private final RecommendShortcutMapper recommendShortcutMapper;
    private final RecommendWidgetMapper recommendWidgetMapper;
    private final RecommendTodoItemMapper recommendTodoItemMapper;
    
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 处理用户登录请求并返回Token
     */
    @Transactional
    public LoginRespDTO login(LoginReqDTO req) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        
        if (user == null || user.getStatus() == USER_STATUS_DISABLED) {
            throw new BizException(ResultCode.PASSWORD_ERROR);
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BizException(ResultCode.PASSWORD_ERROR);
        }

        String role = "ADMIN".equals(user.getRole()) ? "ROLE_ADMIN" : "ROLE_USER";
        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getUsername(), role);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

        redisTemplate.opsForValue().set(
                RedisConstants.KEY_AUTH_REFRESH_TOKEN + user.getUserId(),
                refreshToken, 7, TimeUnit.DAYS);

        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);

        UserRespDTO userVO = new UserRespDTO();
        userVO.setUserId(user.getUserId());
        userVO.setUsername(user.getUsername());
        userVO.setAvatar(user.getAvatar());
        userVO.setRole(user.getRole());
        userVO.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);

        LoginRespDTO vo = new LoginRespDTO();
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);
        vo.setExpiresIn(jwtTokenProvider.getAccessTokenExpire());
        vo.setUserInfo(userVO);
        log.info("用户登录成功 userId={} username={} role={}", user.getUserId(), user.getUsername(), user.getRole());
        return vo;
    }

    /**
     * 处理用户注册请求逻辑
     */
    @Transactional
    public void register(RegisterReqDTO req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new BizException(ResultCode.BAD_REQUEST);
        }
        
        if (userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername())) != null) {
            throw new BizException(ResultCode.USERNAME_EXISTS);
        }

        // 1. 创建普通用户
        User user = new User();
        user.setUserId(IdUtils.genUserId());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole("USER");
        user.setStatus(USER_STATUS_ENABLED);
        userMapper.insert(user);

        // 2. 同步推荐视觉设置配置，若无则使用系统默认兜底配置
        UserConfig config = new UserConfig();
        config.setConfigId(IdUtils.genConfigId());
        config.setUserId(user.getUserId());
        
        RecommendConfig recommendConfig = recommendConfigMapper.selectOne(new LambdaQueryWrapper<RecommendConfig>().last("LIMIT 1"));

        if (recommendConfig != null) {
            config.setSearchEngine(recommendConfig.getSearchEngine());
            config.setBackgroundImage(recommendConfig.getBackgroundImage());
            config.setBackgroundType(recommendConfig.getBackgroundType());
            config.setSearchBoxWidth(recommendConfig.getSearchBoxWidth());
            config.setSearchBoxHeight(recommendConfig.getSearchBoxHeight());
            config.setSearchBoxMarginTop(recommendConfig.getSearchBoxMarginTop());
            config.setIconSize(recommendConfig.getIconSize());
            config.setIconRadius(recommendConfig.getIconRadius());
            config.setIconSpacingX(recommendConfig.getIconSpacingX());
            config.setIconSpacingY(recommendConfig.getIconSpacingY());
            config.setIconTextGap(recommendConfig.getIconTextGap());
            config.setTextSize(recommendConfig.getTextSize());
            config.setIconsMarginTop(recommendConfig.getIconsMarginTop());
            config.setIconsMarginX(recommendConfig.getIconsMarginX());
            config.setTheme(recommendConfig.getTheme());
        } else {
            config.setSearchEngine(SettingsConstants.DEFAULT_SEARCH_ENGINE);
            config.setSearchBoxWidth(SettingsConstants.DEFAULT_SEARCH_BOX_WIDTH);
            config.setSearchBoxHeight(SettingsConstants.DEFAULT_SEARCH_BOX_HEIGHT);
            config.setSearchBoxMarginTop(SettingsConstants.DEFAULT_SEARCH_BOX_MARGIN_TOP);
            config.setIconSize(SettingsConstants.DEFAULT_ICON_SIZE);
            config.setIconRadius(SettingsConstants.DEFAULT_ICON_RADIUS);
            config.setIconSpacingX(SettingsConstants.DEFAULT_ICON_SPACING_X);
            config.setIconSpacingY(SettingsConstants.DEFAULT_ICON_SPACING_Y);
            config.setIconTextGap(SettingsConstants.DEFAULT_ICON_TEXT_GAP);
            config.setTextSize(SettingsConstants.DEFAULT_TEXT_SIZE);
            config.setIconsMarginTop(SettingsConstants.DEFAULT_ICONS_MARGIN_TOP);
            config.setIconsMarginX(SettingsConstants.DEFAULT_ICONS_MARGIN_X);
            config.setTheme(SettingsConstants.DEFAULT_THEME);
            config.setBackgroundType(SettingsConstants.DEFAULT_BACKGROUND_TYPE);
        }
        userConfigMapper.insert(config);

        // 3. 同步首页导航网址与分类，若无数据则降级创建默认常用分类
        List<RecommendCategory> recommendCategories = recommendCategoryMapper.selectList(
                new LambdaQueryWrapper<RecommendCategory>().orderByAsc(RecommendCategory::getSortOrder));

        List<NavCategory> saveCategories = new ArrayList<>();
        List<NavShortcut> saveShortcuts = new ArrayList<>();

        if (recommendCategories != null && !recommendCategories.isEmpty()) {
            for (RecommendCategory recCat : recommendCategories) {
                NavCategory userCat = new NavCategory();
                userCat.setCategoryId(IdUtils.genCategoryId());
                userCat.setUserId(user.getUserId());
                userCat.setName(recCat.getName());
                userCat.setSortOrder(recCat.getSortOrder());
                saveCategories.add(userCat);

                List<RecommendShortcut> recShortcuts = recommendShortcutMapper.selectList(
                        new LambdaQueryWrapper<RecommendShortcut>()
                                .eq(RecommendShortcut::getCategoryId, recCat.getCategoryId())
                                .orderByAsc(RecommendShortcut::getSortOrder));
                if (recShortcuts != null && !recShortcuts.isEmpty()) {
                    for (RecommendShortcut recShortcut : recShortcuts) {
                        NavShortcut userShortcut = new NavShortcut();
                        userShortcut.setShortcutId(IdUtils.genShortcutId());
                        userShortcut.setCategoryId(userCat.getCategoryId());
                        userShortcut.setUserId(user.getUserId());
                        userShortcut.setName(recShortcut.getName());
                        userShortcut.setUrl(recShortcut.getUrl());
                        userShortcut.setIconType(recShortcut.getIconType());
                        userShortcut.setIconValue(recShortcut.getIconValue());
                        userShortcut.setIconColor(recShortcut.getIconColor());
                        userShortcut.setSortOrder(recShortcut.getSortOrder());
                        userShortcut.setClickCount(0L);
                        saveShortcuts.add(userShortcut);
                    }
                }
            }
        } else {
            NavCategory defaultCategory = new NavCategory();
            defaultCategory.setCategoryId(IdUtils.genCategoryId());
            defaultCategory.setUserId(user.getUserId());
            defaultCategory.setName("常用");
            defaultCategory.setSortOrder(0.0);
            saveCategories.add(defaultCategory);
        }

        if (!CollectionUtils.isEmpty(saveCategories)) {
            Db.saveBatch(saveCategories);
        }
        if (!CollectionUtils.isEmpty(saveShortcuts)) {
            Db.saveBatch(saveShortcuts);
        }

        // 4. 同步小组件表
        List<RecommendWidget> recommendWidgets = recommendWidgetMapper.selectList(null);
        List<UserWidget> saveWidgets = new ArrayList<>();
        if (recommendWidgets != null && !recommendWidgets.isEmpty()) {
            for (RecommendWidget recWidget : recommendWidgets) {
                UserWidget userWidget = new UserWidget();
                userWidget.setWidgetId(IdUtils.genWidgetId());
                userWidget.setUserId(user.getUserId());
                userWidget.setType(recWidget.getType());
                userWidget.setStyle(recWidget.getStyle());
                userWidget.setX(recWidget.getX());
                userWidget.setY(recWidget.getY());
                userWidget.setMeta(recWidget.getMeta());
                saveWidgets.add(userWidget);
            }
        }
        if (!CollectionUtils.isEmpty(saveWidgets)) {
            Db.saveBatch(saveWidgets);
        }

        // 5. 同步待办事项表
        List<RecommendTodoItem> recommendTodos = recommendTodoItemMapper.selectList(
                new LambdaQueryWrapper<RecommendTodoItem>().orderByAsc(RecommendTodoItem::getSortOrder));
        List<TodoItem> saveTodos = new ArrayList<>();
        if (recommendTodos != null && !recommendTodos.isEmpty()) {
            for (RecommendTodoItem recTodo : recommendTodos) {
                TodoItem todoItem = new TodoItem();
                todoItem.setTodoId(IdUtils.genTodoId());
                todoItem.setUserId(user.getUserId());
                todoItem.setContent(recTodo.getContent());
                todoItem.setCompleted(recTodo.getCompleted());
                todoItem.setSortOrder(recTodo.getSortOrder());
                saveTodos.add(todoItem);
            }
        }
        if (!CollectionUtils.isEmpty(saveTodos)) {
            Db.saveBatch(saveTodos);
        }

        log.info("用户注册成功 userId={} username={}", user.getUserId(), user.getUsername());
    }

    /**
     * 处理修改密码逻辑
     */
    @Transactional
    public void changePassword(String userId, ChangePasswordReqDTO req) {
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "两次输入新密码不一致");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userMapper.updateById(user);
        log.info("用户密码修改成功 userId={} username={}", user.getUserId(), user.getUsername());
    }

    /**
     * 处理Token刷新请求
     */
    public LoginRespDTO refresh(RefreshTokenReqDTO req) {
        if (!jwtTokenProvider.validateToken(req.getRefreshToken())) {
            throw new BizException(ResultCode.TOKEN_INVALID);
        }
        String userId = jwtTokenProvider.getUserIdFromToken(req.getRefreshToken());
        String storedToken = (String) redisTemplate.opsForValue().get(RedisConstants.KEY_AUTH_REFRESH_TOKEN + userId);

        if (storedToken == null || !storedToken.equals(req.getRefreshToken())) {
            throw new BizException(ResultCode.TOKEN_INVALID);
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }

        String role = "ADMIN".equals(user.getRole()) ? "ROLE_ADMIN" : "ROLE_USER";
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, user.getUsername(), role);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
        redisTemplate.opsForValue().set(RedisConstants.KEY_AUTH_REFRESH_TOKEN + userId, newRefreshToken, 7, TimeUnit.DAYS);

        UserRespDTO userVO = new UserRespDTO();
        userVO.setUserId(user.getUserId());
        userVO.setUsername(user.getUsername());
        userVO.setAvatar(user.getAvatar());
        userVO.setRole(user.getRole());
        userVO.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);

        LoginRespDTO vo = new LoginRespDTO();
        vo.setAccessToken(newAccessToken);
        vo.setRefreshToken(newRefreshToken);
        vo.setUserInfo(userVO);
        vo.setExpiresIn(jwtTokenProvider.getAccessTokenExpire());
        return vo;
    }

    /**
     * 处理用户登出逻辑，清除 Token
     */
    public void logout(String userId, String token) {
        String tokenId;
        try {
            tokenId = jwtTokenProvider.parseToken(token).getId();
        } catch (Exception e) {
            log.error("解析登出Token失败", e);
            return;
        }
        long remaining = jwtTokenProvider.getAccessTokenExpire();
        redisTemplate.opsForValue().set(RedisConstants.KEY_AUTH_BLACKLIST + tokenId, "1", remaining, TimeUnit.SECONDS);
        redisTemplate.delete(RedisConstants.KEY_AUTH_REFRESH_TOKEN + userId);
        log.info("用户登出成功 userId={}", userId);
    }

    /**
     * 获取当前登录用户信息
     */
    public UserRespDTO getCurrentUser(String userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }
        UserRespDTO vo = new UserRespDTO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        vo.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        return vo;
    }
}
