package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.navatation.business.dto.req.auth.LoginReqDTO;
import com.navatation.business.dto.resp.auth.LoginRespDTO;
import com.navatation.business.dto.req.auth.RefreshTokenReqDTO;
import com.navatation.business.dto.req.auth.ChangePasswordReqDTO;
import com.navatation.business.dto.req.auth.RegisterReqDTO;
import com.navatation.business.dto.resp.user.UserRespDTO;
import com.navatation.business.entity.root.RootConfig;
import com.navatation.business.entity.root.RootCategory;
import com.navatation.business.entity.root.RootShortcut;
import com.navatation.business.entity.root.RootWidget;
import com.navatation.business.entity.root.RootUser;
import com.navatation.business.entity.nav.NavCategory;
import com.navatation.business.entity.nav.NavShortcut;
import com.navatation.business.entity.nav.UserWidget;
import com.navatation.business.entity.user.User;
import com.navatation.business.entity.user.UserConfig;
import com.navatation.business.mapper.NavCategoryMapper;
import com.navatation.business.mapper.NavShortcutMapper;
import com.navatation.business.mapper.UserConfigMapper;
import com.navatation.business.mapper.UserMapper;
import com.navatation.business.mapper.RootUserMapper;
import com.navatation.business.mapper.RootConfigMapper;
import com.navatation.business.mapper.RootCategoryMapper;
import com.navatation.business.mapper.RootShortcutMapper;
import com.navatation.business.mapper.RootWidgetMapper;
import com.navatation.business.mapper.UserWidgetMapper;
import com.navatation.common.BizException;
import com.navatation.common.RedisConstants;
import com.navatation.common.ResultCode;
import com.navatation.common.IdUtils;
import com.navatation.framework.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.navatation.business.constant.SettingsConstants;

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
    private final RootUserMapper rootUserMapper;
    private final UserConfigMapper userConfigMapper;
    private final NavCategoryMapper navCategoryMapper;
    private final NavShortcutMapper navShortcutMapper;
    private final RootConfigMapper rootConfigMapper;
    private final RootCategoryMapper rootCategoryMapper;
    private final RootShortcutMapper rootShortcutMapper;
    private final RootWidgetMapper rootWidgetMapper;
    private final UserWidgetMapper userWidgetMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

        /**
     * login 方法
     */
    @Transactional
    public LoginRespDTO login(LoginReqDTO req) {
        // 先检查超级管理员
        RootUser rootUser = rootUserMapper.selectOne(
                new LambdaQueryWrapper<RootUser>().eq(RootUser::getUsername, req.getUsername()));
        
        if (rootUser != null) {
            if (rootUser.getStatus() == USER_STATUS_DISABLED) {
                throw new BizException(ResultCode.PASSWORD_ERROR);
            }
            if (!passwordEncoder.matches(req.getPassword(), rootUser.getPassword())) {
                throw new BizException(ResultCode.PASSWORD_ERROR);
            }

            String accessToken = jwtTokenProvider.generateAccessToken(rootUser.getUserId(), rootUser.getUsername());
            String refreshToken = jwtTokenProvider.generateRefreshToken(rootUser.getUserId());

            redisTemplate.opsForValue().set(
                    RedisConstants.KEY_AUTH_REFRESH_TOKEN + rootUser.getUserId(),
                    refreshToken, 7, TimeUnit.DAYS);

            rootUser.setLastLoginAt(LocalDateTime.now());
            rootUserMapper.updateById(rootUser);

            UserRespDTO userVO = new UserRespDTO();
            userVO.setUserId(rootUser.getUserId());
            userVO.setUsername(rootUser.getUsername());
            userVO.setAvatar(rootUser.getAvatar());
            userVO.setRole("ADMIN");
            userVO.setCreatedAt(rootUser.getCreatedAt() != null ? rootUser.getCreatedAt().toString() : null);

            LoginRespDTO vo = new LoginRespDTO();
            vo.setAccessToken(accessToken);
            vo.setRefreshToken(refreshToken);
            vo.setExpiresIn(jwtTokenProvider.getAccessTokenExpire());
            vo.setUserInfo(userVO);
            log.info("管理员登录成功 userId={} username={}", rootUser.getUserId(), rootUser.getUsername());
            return vo;
        }

        // 若不是超级管理员，检查普通用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        
        if (user == null || user.getStatus() == USER_STATUS_DISABLED) {
            throw new BizException(ResultCode.PASSWORD_ERROR);
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BizException(ResultCode.PASSWORD_ERROR);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getUserId(), user.getUsername());
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
        userVO.setRole("USER");
        userVO.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);

        LoginRespDTO vo = new LoginRespDTO();
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);
        vo.setExpiresIn(jwtTokenProvider.getAccessTokenExpire());
        vo.setUserInfo(userVO);
        log.info("用户登录成功 userId={} username={}", user.getUserId(), user.getUsername());
        return vo;
    }

        /**
     * register 方法
     */
    @Transactional
    public void register(RegisterReqDTO req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new BizException(ResultCode.BAD_REQUEST);
        }
        
        // 检查根用户或普通用户是否重名
        if (rootUserMapper.selectOne(new LambdaQueryWrapper<RootUser>().eq(RootUser::getUsername, req.getUsername())) != null ||
            userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername())) != null) {
            throw new BizException(ResultCode.USERNAME_EXISTS);
        }

        // 1. 创建普通用户
        User user = new User();
        user.setUserId(IdUtils.genUserId());
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setStatus(USER_STATUS_ENABLED);
        userMapper.insert(user);

        // 2. 查找超级管理员 (ADMIN) 的 userId，以作为拷贝的数据源
        RootUser admin = rootUserMapper.selectOne(new LambdaQueryWrapper<RootUser>().last("LIMIT 1"));
        String adminId = admin != null ? admin.getUserId() : null;

        // 3. 同步超级管理员的视觉设置配置，若无则使用系统默认兜底配置
        UserConfig config = new UserConfig();
        config.setConfigId(IdUtils.genConfigId());
        config.setUserId(user.getUserId());
        
        RootConfig adminConfig = null;
        if (adminId != null) {
            adminConfig = rootConfigMapper.selectOne(
                    new LambdaQueryWrapper<RootConfig>().eq(RootConfig::getUserId, adminId));
        }

        if (adminConfig != null) {
            config.setSearchEngine(adminConfig.getSearchEngine());
            config.setBackgroundImage(adminConfig.getBackgroundImage());
            config.setBackgroundType(adminConfig.getBackgroundType());
            config.setSearchBoxWidth(adminConfig.getSearchBoxWidth());
            config.setSearchBoxHeight(adminConfig.getSearchBoxHeight());
            config.setSearchBoxMarginTop(adminConfig.getSearchBoxMarginTop());
            config.setIconSize(adminConfig.getIconSize());
            config.setIconRadius(adminConfig.getIconRadius());
            config.setIconSpacingX(adminConfig.getIconSpacingX());
            config.setIconSpacingY(adminConfig.getIconSpacingY());
            config.setIconTextGap(adminConfig.getIconTextGap());
            config.setTextSize(adminConfig.getTextSize());
            config.setIconsMarginTop(adminConfig.getIconsMarginTop());
            config.setIconsMarginX(adminConfig.getIconsMarginX());
            config.setTheme(adminConfig.getTheme());
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

        // 4. 同步首页导航网址与分类，若无管理员数据则降级创建默认常用分类
        List<RootCategory> adminCategories = null;
        if (adminId != null) {
            adminCategories = rootCategoryMapper.selectList(
                    new LambdaQueryWrapper<RootCategory>()
                            .eq(RootCategory::getUserId, adminId)
                            .orderByAsc(RootCategory::getSortOrder));
        }

        if (adminCategories != null && !adminCategories.isEmpty()) {
            for (RootCategory adminCat : adminCategories) {
                NavCategory userCat = new NavCategory();
                userCat.setCategoryId(IdUtils.genCategoryId());
                userCat.setUserId(user.getUserId());
                userCat.setName(adminCat.getName());
                userCat.setSortOrder(adminCat.getSortOrder());
                navCategoryMapper.insert(userCat);

                List<RootShortcut> adminShortcuts = rootShortcutMapper.selectList(
                        new LambdaQueryWrapper<RootShortcut>()
                                .eq(RootShortcut::getCategoryId, adminCat.getCategoryId())
                                .orderByAsc(RootShortcut::getSortOrder));
                if (adminShortcuts != null && !adminShortcuts.isEmpty()) {
                    for (RootShortcut adminShortcut : adminShortcuts) {
                        NavShortcut userShortcut = new NavShortcut();
                        userShortcut.setShortcutId(IdUtils.genShortcutId());
                        userShortcut.setCategoryId(userCat.getCategoryId());
                        userShortcut.setUserId(user.getUserId());
                        userShortcut.setName(adminShortcut.getName());
                        userShortcut.setUrl(adminShortcut.getUrl());
                        userShortcut.setIconType(adminShortcut.getIconType());
                        userShortcut.setIconValue(adminShortcut.getIconValue());
                        userShortcut.setIconColor(adminShortcut.getIconColor());
                        userShortcut.setSortOrder(adminShortcut.getSortOrder());
                        userShortcut.setClickCount(0L);
                        navShortcutMapper.insert(userShortcut);
                    }
                }
            }
        } else {
            NavCategory defaultCategory = new NavCategory();
            defaultCategory.setCategoryId(IdUtils.genCategoryId());
            defaultCategory.setUserId(user.getUserId());
            defaultCategory.setName("常用");
            defaultCategory.setSortOrder(0.0);
            navCategoryMapper.insert(defaultCategory);
        }

        // 5. 同步小组件表，拷贝所有时钟、专注等小组件卡片
        List<RootWidget> adminWidgets = null;
        if (adminId != null) {
            adminWidgets = rootWidgetMapper.selectList(
                    new LambdaQueryWrapper<RootWidget>().eq(RootWidget::getUserId, adminId));
        }

        if (adminWidgets != null && !adminWidgets.isEmpty()) {
            for (RootWidget adminWidget : adminWidgets) {
                UserWidget userWidget = new UserWidget();
                userWidget.setWidgetId(IdUtils.genWidgetId());
                userWidget.setUserId(user.getUserId());
                userWidget.setType(adminWidget.getType());
                userWidget.setStyle(adminWidget.getStyle());
                userWidget.setX(adminWidget.getX());
                userWidget.setY(adminWidget.getY());
                userWidget.setMeta(adminWidget.getMeta());
                userWidgetMapper.insert(userWidget);
            }
        }

        log.info("用户注册成功 userId={} username={}", user.getUserId(), user.getUsername());
    }

        /**
     * changePassword 方法
     */
    @Transactional
    public void changePassword(String userId, ChangePasswordReqDTO req) {
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "两次输入新密码不一致");
        }

        RootUser rootUser = rootUserMapper.selectById(userId);
        if (rootUser != null) {
            if (!passwordEncoder.matches(req.getOldPassword(), rootUser.getPassword())) {
                throw new BizException(ResultCode.BAD_REQUEST.getCode(), "原密码错误");
            }
            rootUser.setPassword(passwordEncoder.encode(req.getNewPassword()));
            rootUserMapper.updateById(rootUser);
            log.info("超级管理员密码修改成功 userId={} username={}", rootUser.getUserId(), rootUser.getUsername());
            return;
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
     * refresh 方法
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

        RootUser rootUser = rootUserMapper.selectById(userId);
        if (rootUser != null) {
            String newAccessToken = jwtTokenProvider.generateAccessToken(userId, rootUser.getUsername());
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
            redisTemplate.opsForValue().set(RedisConstants.KEY_AUTH_REFRESH_TOKEN + userId, newRefreshToken, 7, TimeUnit.DAYS);
            
            UserRespDTO userVO = new UserRespDTO();
            userVO.setUserId(rootUser.getUserId());
            userVO.setUsername(rootUser.getUsername());
            userVO.setAvatar(rootUser.getAvatar());
            userVO.setRole("ADMIN");
            userVO.setCreatedAt(rootUser.getCreatedAt() != null ? rootUser.getCreatedAt().toString() : null);

            LoginRespDTO vo = new LoginRespDTO();
            vo.setAccessToken(newAccessToken);
            vo.setRefreshToken(newRefreshToken);
            vo.setUserInfo(userVO);
            vo.setExpiresIn(jwtTokenProvider.getAccessTokenExpire());
            return vo;
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(userId, user.getUsername());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);
        redisTemplate.opsForValue().set(RedisConstants.KEY_AUTH_REFRESH_TOKEN + userId, newRefreshToken, 7, TimeUnit.DAYS);

        UserRespDTO userVO = new UserRespDTO();
        userVO.setUserId(user.getUserId());
        userVO.setUsername(user.getUsername());
        userVO.setAvatar(user.getAvatar());
        userVO.setRole("USER");
        userVO.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);

        LoginRespDTO vo = new LoginRespDTO();
        vo.setAccessToken(newAccessToken);
        vo.setRefreshToken(newRefreshToken);
        vo.setUserInfo(userVO);
        vo.setExpiresIn(jwtTokenProvider.getAccessTokenExpire());
        return vo;
    }

        /**
     * logout 方法
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
     * getCurrentUser 方法
     */
    public UserRespDTO getCurrentUser(String userId) {
        RootUser rootUser = rootUserMapper.selectById(userId);
        if (rootUser != null) {
            UserRespDTO vo = new UserRespDTO();
            vo.setUserId(rootUser.getUserId());
            vo.setUsername(rootUser.getUsername());
            vo.setAvatar(rootUser.getAvatar());
            vo.setRole("ADMIN");
            vo.setCreatedAt(rootUser.getCreatedAt() != null ? rootUser.getCreatedAt().toString() : null);
            return vo;
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }
        UserRespDTO vo = new UserRespDTO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setAvatar(user.getAvatar());
        vo.setRole("USER");
        vo.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null);
        return vo;
    }
}
