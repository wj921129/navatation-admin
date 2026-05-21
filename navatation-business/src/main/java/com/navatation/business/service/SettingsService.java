package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.navatation.business.dto.SettingsRequest;
import com.navatation.business.dto.SettingsVO;
import com.navatation.business.dto.WallpaperVO;
import com.navatation.business.entity.UserConfig;
import com.navatation.business.mapper.UserConfigMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description 用户设置服务，处理用户配置的查询、保存、局部更新及壁纸上传 */
@Service
@RequiredArgsConstructor
public class SettingsService {

    private static final Logger log = LoggerFactory.getLogger(SettingsService.class);

    private final UserConfigMapper userConfigMapper;

    @Value("${app.upload.wallpaper-path}")
    private String wallpaperPath;

    @Value("${app.upload.local-wallpaper-path}")
    private String localWallpaperPath;

    /**
     * 获取用户设置，不存在则创建默认配置
     * @param userId 用户ID
     * @return 用户设置 */
    public SettingsVO getSettings(Long userId) {
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
     * @param req 设置请求 */
    public void saveSettings(Long userId, SettingsRequest req) {
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
     * @param req 设置请求 */
    public void patchSettings(Long userId, SettingsRequest req) {
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
     * @return 壁纸URL */
    public WallpaperVO uploadWallpaper(Long userId, MultipartFile file) {
        try {
            String targetDir = wallpaperPath + java.io.File.separator + "U" + userId;
            String uniqueFileName = com.navatation.common.FileUploadUtil.saveFile(file, targetDir);
            
            WallpaperVO vo = new WallpaperVO();
            vo.setWallpaperUrl("/uploads/back_ground/custom/U" + userId + "/" + uniqueFileName);
            log.info("壁纸上传成功 userId={}, filename={}", userId, uniqueFileName);
            return vo;
        } catch (Exception e) {
            log.error("壁纸上传失败 userId={}", userId, e);
            throw new RuntimeException("壁纸上传失败", e);
        }
    }

    /**
     * 随机从本地壁纸目录中选择一个壁纸返回
     * @return 壁纸VO */
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
                vo.setWallpaperUrl("https://images.unsplash.com/photo-1598439473183-42c9301db5dc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=2400");
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
            vo.setWallpaperUrl("https://images.unsplash.com/photo-1598439473183-42c9301db5dc?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=2400");
            return vo;
        }
    }

    /** 创建默认用户配置 */
    private UserConfig createDefault(Long userId) {
        UserConfig config = new UserConfig();
        config.setUserId(userId);
        config.setSearchEngine("google");
        config.setSearchBoxWidth(100);
        config.setSearchBoxHeight(64);
        config.setSearchBoxMarginTop(192);
        config.setIconSize(64);
        config.setIconRadius(50);
        config.setIconSpacingX(32);
        config.setIconSpacingY(48);
        config.setIconTextGap(12);
        config.setTextSize(14);
        config.setIconsMarginTop(64);
        config.setTheme("dark");
        config.setBackgroundType("URL");
        userConfigMapper.insert(config);
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
        vo.setTheme(config.getTheme());
        return vo;
    }
}
