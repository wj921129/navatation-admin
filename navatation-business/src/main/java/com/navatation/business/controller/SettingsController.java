package com.navatation.business.controller;

import com.navatation.business.dto.req.settings.SettingsReqDTO;
import com.navatation.business.dto.resp.settings.SettingsRespDTO;
import com.navatation.business.dto.resp.settings.WallpaperRespDTO;
import com.navatation.business.service.SettingsService;
import com.navatation.common.Result;
import com.navatation.framework.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author admin
 * @CreateTime 2026-05-15
 * @Description 用户设置控制器，处理用户配置的查询、保存、局部更新及壁纸上传
 */
@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingsController {

    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);

    private final SettingsService settingsService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public Result<SettingsRespDTO> getSettings(@RequestHeader("Authorization") String auth) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("获取用户设置 入参:userId={}", userId);
        SettingsRespDTO result = settingsService.getSettings(userId);
        log.info("获取用户设置 出参:theme={}", result.getTheme());
        return Result.success(result);
    }

    @PutMapping
    public Result<?> saveSettings(@RequestHeader("Authorization") String auth,
                                   @RequestBody SettingsReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("保存用户设置 入参:userId={}", userId);
        settingsService.saveSettings(userId, req);
        log.info("保存用户设置 出参:success=true");
        return Result.success("配置保存成功", null);
    }

    @PatchMapping
    public Result<?> patchSettings(@RequestHeader("Authorization") String auth,
                                    @RequestBody SettingsReqDTO req) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("局部更新用户设置 入参:userId={}", userId);
        settingsService.patchSettings(userId, req);
        log.info("局部更新用户设置 出参:success=true");
        return Result.success("配置更新成功", null);
    }

    @PatchMapping("/search-engine")
    public Result<?> switchSearchEngine(@RequestHeader("Authorization") String auth,
                                        @RequestParam("engine") String engine) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("切换搜索引擎 入参:userId={}, engine={}", userId, engine);
        SettingsReqDTO req = new SettingsReqDTO();
        req.setSearchEngine(engine);
        settingsService.patchSettings(userId, req);
        log.info("切换搜索引擎 出参:success=true");
        return Result.success("搜索引擎切换成功", null);
    }

    @PostMapping("/wallpaper/upload")
    public Result<WallpaperRespDTO> uploadWallpaper(@RequestHeader("Authorization") String auth,
                                                @RequestParam("file") MultipartFile file) {
        String userId = jwtTokenProvider.getUserIdFromAuthHeader(auth);
        log.info("上传壁纸 入参:userId={},filename={}", userId, file.getOriginalFilename());
        WallpaperRespDTO result = settingsService.uploadWallpaper(userId, file);
        log.info("上传壁纸 出参:wallpaperUrl={}", result.getWallpaperUrl());
        return Result.success("上传成功", result);
    }

        /**
     * 获取系统本地的随机壁纸
     */
    @GetMapping("/wallpaper/random")
    public Result<WallpaperRespDTO> getRandomWallpaper() {
        log.info("获取随机壁纸 入参:无");
        WallpaperRespDTO result = settingsService.getRandomWallpaper();
        log.info("获取随机壁纸 出参:wallpaperUrl={}", result.getWallpaperUrl());
        return Result.success("获取随机壁纸成功", result);
    }

    @GetMapping("/debug/resource")
    public Result<String> debugResource(@org.springframework.beans.factory.annotation.Value("${app.upload.local-wallpaper-path}") String path) {
        try {
            org.springframework.core.io.ResourceLoader rl = new org.springframework.core.io.DefaultResourceLoader();
            org.springframework.core.io.Resource r = rl.getResource("file:" + path + "/wallpaper_4.jpg");
            return Result.success("Debug info", "Path: file:" + path + "/wallpaper_4.jpg, Exists: " + r.exists() + ", URL: " + r.getURL() + ", File: " + r.getFile().getAbsolutePath());
        } catch(Exception e) {
            return Result.success("Debug info", "Exception: " + e.getMessage());
        }
    }
}
