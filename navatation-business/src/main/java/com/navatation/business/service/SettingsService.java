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

    @Value("${app.upload.path}")
    private String uploadPath;

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
            String uploadDir = uploadPath + "/wallpapers/user_" + userId;
            Files.createDirectories(Paths.get(uploadDir));
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filepath = Paths.get(uploadDir, filename);
            file.transferTo(filepath.toFile());

            WallpaperVO vo = new WallpaperVO();
            vo.setWallpaperUrl("/uploads/wallpapers/user_" + userId + "/" + filename);
            return vo;
        } catch (IOException e) {
            log.error("壁纸上传失败 userId={}", userId, e);
            throw new RuntimeException("壁纸上传失败", e);
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
