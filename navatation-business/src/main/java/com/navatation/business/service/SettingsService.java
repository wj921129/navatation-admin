package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.navatation.business.constant.SettingsConstants;
import com.navatation.business.dto.SettingsRequest;
import com.navatation.business.dto.SettingsVO;
import com.navatation.business.dto.WallpaperVO;
import com.navatation.business.entity.recommend.RecommendConfig;
import com.navatation.business.entity.user.User;
import com.navatation.business.entity.user.UserConfig;
import com.navatation.business.mapper.RecommendConfigMapper;
import com.navatation.business.mapper.RootConfigMapper;
import com.navatation.business.mapper.UserConfigMapper;
import com.navatation.business.mapper.UserMapper;
import com.navatation.business.mapper.RootUserMapper;
import com.navatation.common.IdUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @Author admin
 * @CreateTime 2026-05-15
 * @Description 用户设置服务，处理用户配置的查询、保存、局部更新及壁纸上传
 */
@Service
@RequiredArgsConstructor
public class SettingsService {

    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);

    private final UserConfigMapper userConfigMapper;
    private final RootConfigMapper rootConfigMapper;
    private final RecommendConfigMapper recommendConfigMapper;
    private final UserMapper userMapper;

    private final RootUserMapper rootUserMapper;

    private boolean isAdmin(String userId) {
        return rootUserMapper.selectById(userId) != null;
    }

    @Value("${app.upload.wallpaper-path}")
    private String wallpaperPath;

    @Value("${app.upload.local-wallpaper-path}")
    private String localWallpaperPath;

    /**
     * 获取用户设置，不存在则创建默认配置
     * @param userId 用户ID
     * @return 用户设置
     */
    public SettingsVO getSettings(String userId) {
        if (isAdmin(userId)) {
            com.navatation.business.entity.root.RootConfig rootConfig = rootConfigMapper.selectOne(
                    new LambdaQueryWrapper<com.navatation.business.entity.root.RootConfig>().eq(com.navatation.business.entity.root.RootConfig::getUserId, userId));
            if (rootConfig == null) {
                rootConfig = createDefaultRoot(userId);
            }
            return toVO(rootConfig);
        }

        UserConfig config = userConfigMapper.selectOne(
                new LambdaQueryWrapper<UserConfig>().eq(UserConfig::getUserId, userId));
        if (config == null) {
            config = createDefault(userId);
        }
        return toVO(config);
    }

    /**
     * 全量保存用户设置
     * @param userId 用户ID
     * @param req 设置请求
     */
    public void saveSettings(String userId, SettingsRequest req) {
        if (isAdmin(userId)) {
            com.navatation.business.entity.root.RootConfig rootConfig = rootConfigMapper.selectOne(
                    new LambdaQueryWrapper<com.navatation.business.entity.root.RootConfig>().eq(com.navatation.business.entity.root.RootConfig::getUserId, userId));
            if (rootConfig == null) {
                rootConfig = createDefaultRoot(userId);
            }
            applyRequest(rootConfig, req);
            rootConfigMapper.updateById(rootConfig);
            log.info("保存管理员设置成功 userId={}", userId);
            return;
        }

        UserConfig config = userConfigMapper.selectOne(
                new LambdaQueryWrapper<UserConfig>().eq(UserConfig::getUserId, userId));
        if (config == null) {
            config = createDefault(userId);
        }
        applyRequest(config, req);
        userConfigMapper.updateById(config);
        log.info("保存用户设置成功 userId={}", userId);
    }

    /**
     * 局部更新用户设置（仅更新非null字段）
     * @param userId 用户ID
     * @param req 设置请求
     */
    public void patchSettings(String userId, SettingsRequest req) {
        if (isAdmin(userId)) {
            com.navatation.business.entity.root.RootConfig rootConfig = rootConfigMapper.selectOne(
                    new LambdaQueryWrapper<com.navatation.business.entity.root.RootConfig>().eq(com.navatation.business.entity.root.RootConfig::getUserId, userId));
            if (rootConfig == null) {
                rootConfig = createDefaultRoot(userId);
            }
            applyRequest(rootConfig, req);
            rootConfigMapper.updateById(rootConfig);
            log.info("局部更新管理员设置成功 userId={}", userId);
            return;
        }

        UserConfig config = userConfigMapper.selectOne(
                new LambdaQueryWrapper<UserConfig>().eq(UserConfig::getUserId, userId));
        if (config == null) {
            config = createDefault(userId);
        }
        applyRequest(config, req);
        userConfigMapper.updateById(config);
        log.info("局部更新用户设置成功 userId={}", userId);
    }

    /**
     * 上传壁纸文件
     * @param userId 用户ID
     * @param file 壁纸文件
     * @return 壁纸URL
     */
    public WallpaperVO uploadWallpaper(String userId, MultipartFile file) {
        try {
            String targetDir = wallpaperPath + java.io.File.separator + userId;
            String uniqueFileName = com.navatation.common.FileUploadUtil.saveFile(file, targetDir);
            
            WallpaperVO vo = new WallpaperVO();
            vo.setWallpaperUrl("/uploads/back_ground/custom/" + userId + "/" + uniqueFileName);
            log.info("壁纸上传成功 userId={}, filename={}", userId, uniqueFileName);
            return vo;
        } catch (Exception e) {
            log.error("壁纸上传失败 userId={}", userId, e);
            throw new RuntimeException("壁纸上传失败", e);
        }
    }

    /**
     * 随机从本地壁纸目录中选择一个壁纸返回
     * @return 壁纸VO
     */
    public WallpaperVO getRandomWallpaper() {
        try {
            File dir = new File(localWallpaperPath);
            if (!dir.exists()) {
                // 目录不存在则自动创建，支持3次重试
                com.navatation.common.FileUploadUtil.createDirectoryWithRetry(localWallpaperPath);
            }

            // 过滤合法图片文件
            File[] files = dir.listFiles((dirObj, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                        || lower.endsWith(".webp") || lower.endsWith(".gif");
            });

            // 卫语句：如果没有找到任何壁纸文件，返回系统默认兜底壁纸
            if (files == null || files.length == 0) {
                log.warn("本地壁纸目录为空，返回系统默认兜底壁纸: {}", localWallpaperPath);
                WallpaperVO vo = new WallpaperVO();
                vo.setWallpaperUrl(SettingsConstants.DEFAULT_WALLPAPER);
                return vo;
            }

            // 随机选择一个
            int idx = java.util.concurrent.ThreadLocalRandom.current().nextInt(files.length);
            File chosenFile = files[idx];

            WallpaperVO vo = new WallpaperVO();
            vo.setWallpaperUrl("/uploads/back_ground/local/" + chosenFile.getName());
            log.info("随机壁纸获取成功 filename={}", chosenFile.getName());
            return vo;
        } catch (Exception e) {
            log.error("获取随机壁纸异常，进行兜底返回", e);
            WallpaperVO vo = new WallpaperVO();
            vo.setWallpaperUrl(SettingsConstants.DEFAULT_WALLPAPER);
            return vo;
        }
    }

    /** 创建默认用户配置 */
    private UserConfig createDefault(String userId) {
        UserConfig config = new UserConfig();
        config.setConfigId(IdUtils.genConfigId());
        config.setUserId(userId);
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
        userConfigMapper.insert(config);
        return config;
    }

    /** 创建默认管理员配置 */
    private com.navatation.business.entity.root.RootConfig createDefaultRoot(String userId) {
        com.navatation.business.entity.root.RootConfig config = new com.navatation.business.entity.root.RootConfig();
        config.setConfigId(IdUtils.genConfigId());
        config.setUserId(userId);
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
        rootConfigMapper.insert(config);
        return config;
    }

    /** 将请求参数应用到配置实体 */
    private void applyRequest(UserConfig config, SettingsRequest req) {
        if (req.getSearchEngine() != null) {
            config.setSearchEngine(req.getSearchEngine());
        }
        if (req.getBackgroundImage() != null) {
            config.setBackgroundImage(req.getBackgroundImage());
        }
        if (req.getBackgroundType() != null) {
            config.setBackgroundType(req.getBackgroundType());
        }
        if (req.getSearchBoxWidth() != null) {
            config.setSearchBoxWidth(req.getSearchBoxWidth());
        }
        if (req.getSearchBoxHeight() != null) {
            config.setSearchBoxHeight(req.getSearchBoxHeight());
        }
        if (req.getSearchBoxMarginTop() != null) {
            config.setSearchBoxMarginTop(req.getSearchBoxMarginTop());
        }
        if (req.getIconSize() != null) {
            config.setIconSize(req.getIconSize());
        }
        if (req.getIconRadius() != null) {
            config.setIconRadius(req.getIconRadius());
        }
        if (req.getIconSpacingX() != null) {
            config.setIconSpacingX(req.getIconSpacingX());
        }
        if (req.getIconSpacingY() != null) {
            config.setIconSpacingY(req.getIconSpacingY());
        }
        if (req.getIconTextGap() != null) {
            config.setIconTextGap(req.getIconTextGap());
        }
        if (req.getTextSize() != null) {
            config.setTextSize(req.getTextSize());
        }
        if (req.getIconsMarginTop() != null) {
            config.setIconsMarginTop(req.getIconsMarginTop());
        }
        if (req.getIconsMarginX() != null) {
            config.setIconsMarginX(req.getIconsMarginX());
        }
        if (req.getTheme() != null) {
            config.setTheme(req.getTheme());
        }
    }

    /** 实体转VO */
    private SettingsVO toVO(UserConfig config) {
        SettingsVO vo = new SettingsVO();
        vo.setSearchEngine(config.getSearchEngine());
        vo.setBackgroundImage(config.getBackgroundImage());
        vo.setBackgroundType(config.getBackgroundType());
        vo.setSearchBoxWidth(config.getSearchBoxWidth());
        vo.setSearchBoxHeight(config.getSearchBoxHeight());
        vo.setSearchBoxMarginTop(config.getSearchBoxMarginTop());
        vo.setIconSize(config.getIconSize());
        vo.setIconRadius(config.getIconRadius());
        vo.setIconSpacingX(config.getIconSpacingX());
        vo.setIconSpacingY(config.getIconSpacingY());
        vo.setIconTextGap(config.getIconTextGap());
        vo.setTextSize(config.getTextSize());
        vo.setIconsMarginTop(config.getIconsMarginTop());
        vo.setIconsMarginX(config.getIconsMarginX());
        vo.setTheme(config.getTheme());
        return vo;
    }

    private void applyRequest(com.navatation.business.entity.root.RootConfig config, SettingsRequest req) {
        if (req.getSearchEngine() != null) config.setSearchEngine(req.getSearchEngine());
        if (req.getBackgroundImage() != null) config.setBackgroundImage(req.getBackgroundImage());
        if (req.getBackgroundType() != null) config.setBackgroundType(req.getBackgroundType());
        if (req.getSearchBoxWidth() != null) config.setSearchBoxWidth(req.getSearchBoxWidth());
        if (req.getSearchBoxHeight() != null) config.setSearchBoxHeight(req.getSearchBoxHeight());
        if (req.getSearchBoxMarginTop() != null) config.setSearchBoxMarginTop(req.getSearchBoxMarginTop());
        if (req.getIconSize() != null) config.setIconSize(req.getIconSize());
        if (req.getIconRadius() != null) config.setIconRadius(req.getIconRadius());
        if (req.getIconSpacingX() != null) config.setIconSpacingX(req.getIconSpacingX());
        if (req.getIconSpacingY() != null) config.setIconSpacingY(req.getIconSpacingY());
        if (req.getIconTextGap() != null) config.setIconTextGap(req.getIconTextGap());
        if (req.getTextSize() != null) config.setTextSize(req.getTextSize());
        if (req.getIconsMarginTop() != null) config.setIconsMarginTop(req.getIconsMarginTop());
        if (req.getIconsMarginX() != null) config.setIconsMarginX(req.getIconsMarginX());
        if (req.getTheme() != null) config.setTheme(req.getTheme());
    }

    private SettingsVO toVO(com.navatation.business.entity.root.RootConfig config) {
        SettingsVO vo = new SettingsVO();
        vo.setSearchEngine(config.getSearchEngine());
        vo.setBackgroundImage(config.getBackgroundImage());
        vo.setBackgroundType(config.getBackgroundType());
        vo.setSearchBoxWidth(config.getSearchBoxWidth());
        vo.setSearchBoxHeight(config.getSearchBoxHeight());
        vo.setSearchBoxMarginTop(config.getSearchBoxMarginTop());
        vo.setIconSize(config.getIconSize());
        vo.setIconRadius(config.getIconRadius());
        vo.setIconSpacingX(config.getIconSpacingX());
        vo.setIconSpacingY(config.getIconSpacingY());
        vo.setIconTextGap(config.getIconTextGap());
        vo.setTextSize(config.getTextSize());
        vo.setIconsMarginTop(config.getIconsMarginTop());
        vo.setIconsMarginX(config.getIconsMarginX());
        vo.setTheme(config.getTheme());
        return vo;
    }

    private void applyRequest(RecommendConfig config, SettingsRequest req) {
        if (req.getSearchEngine() != null) config.setSearchEngine(req.getSearchEngine());
        if (req.getBackgroundImage() != null) config.setBackgroundImage(req.getBackgroundImage());
        if (req.getBackgroundType() != null) config.setBackgroundType(req.getBackgroundType());
        if (req.getSearchBoxWidth() != null) config.setSearchBoxWidth(req.getSearchBoxWidth());
        if (req.getSearchBoxHeight() != null) config.setSearchBoxHeight(req.getSearchBoxHeight());
        if (req.getSearchBoxMarginTop() != null) config.setSearchBoxMarginTop(req.getSearchBoxMarginTop());
        if (req.getIconSize() != null) config.setIconSize(req.getIconSize());
        if (req.getIconRadius() != null) config.setIconRadius(req.getIconRadius());
        if (req.getIconSpacingX() != null) config.setIconSpacingX(req.getIconSpacingX());
        if (req.getIconSpacingY() != null) config.setIconSpacingY(req.getIconSpacingY());
        if (req.getIconTextGap() != null) config.setIconTextGap(req.getIconTextGap());
        if (req.getTextSize() != null) config.setTextSize(req.getTextSize());
        if (req.getIconsMarginTop() != null) config.setIconsMarginTop(req.getIconsMarginTop());
        if (req.getIconsMarginX() != null) config.setIconsMarginX(req.getIconsMarginX());
        if (req.getTheme() != null) config.setTheme(req.getTheme());
    }

    private SettingsVO toVO(RecommendConfig config) {
        SettingsVO vo = new SettingsVO();
        vo.setSearchEngine(config.getSearchEngine());
        vo.setBackgroundImage(config.getBackgroundImage());
        vo.setBackgroundType(config.getBackgroundType());
        vo.setSearchBoxWidth(config.getSearchBoxWidth());
        vo.setSearchBoxHeight(config.getSearchBoxHeight());
        vo.setSearchBoxMarginTop(config.getSearchBoxMarginTop());
        vo.setIconSize(config.getIconSize());
        vo.setIconRadius(config.getIconRadius());
        vo.setIconSpacingX(config.getIconSpacingX());
        vo.setIconSpacingY(config.getIconSpacingY());
        vo.setIconTextGap(config.getIconTextGap());
        vo.setTextSize(config.getTextSize());
        vo.setIconsMarginTop(config.getIconsMarginTop());
        vo.setIconsMarginX(config.getIconsMarginX());
        vo.setTheme(config.getTheme());
        return vo;
    }
}
