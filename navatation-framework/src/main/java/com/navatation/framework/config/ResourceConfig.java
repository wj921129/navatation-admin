package com.navatation.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * 静态资源映射配置
 * 将 /uploads/** 请求映射到配置文件指定的上传目录，
 * 使上传的壁纸、图标等文件可通过 HTTP 直接访问
 */
@Configuration
public class ResourceConfig implements WebMvcConfigurer {

    @Value("${app.upload.icon-path}")
    private String iconPath;

    @Value("${app.upload.wallpaper-path}")
    private String wallpaperPath;

    @Value("${app.upload.local-wallpaper-path}")
    private String localWallpaperPath;

    @Value("${app.upload.sys-icon-path}")
    private String sysIconPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        CacheControl cacheControl = CacheControl
                .maxAge(30, TimeUnit.DAYS)
                .cachePublic();

        registry.addResourceHandler("/uploads/icon/custom/**")
                .addResourceLocations(getResourceLocation(iconPath))
                .setCacheControl(cacheControl);

        registry.addResourceHandler("/uploads/bg_custom/**")
                .addResourceLocations(getResourceLocation(wallpaperPath))
                .setCacheControl(cacheControl);

        registry.addResourceHandler("/uploads/sys_data/bg_img/**")
                .addResourceLocations(getResourceLocation(localWallpaperPath))
                .setCacheControl(cacheControl);

        registry.addResourceHandler("/uploads/icon/sys/**")
                .addResourceLocations(getResourceLocation(sysIconPath))
                .setCacheControl(cacheControl);
    }

    private String getResourceLocation(String path) {
        String absolutePath = java.nio.file.Paths.get(path).toAbsolutePath().normalize().toString();
        // 替换 Windows 反斜杠为正斜杠，生成标准的 file 路径
        absolutePath = absolutePath.replace("\\", "/");
        if (!absolutePath.startsWith("/")) {
            absolutePath = "/" + absolutePath;
        }
        if (!absolutePath.endsWith("/")) {
            absolutePath = absolutePath + "/";
        }
        return "file:" + absolutePath;
    }
}
