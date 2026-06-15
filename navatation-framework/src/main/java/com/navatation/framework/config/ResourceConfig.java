package com.navatation.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
        String iconAbsolutePath = java.nio.file.Paths.get(iconPath).toAbsolutePath().normalize().toUri().toString();
        String wallpaperAbsolutePath = java.nio.file.Paths.get(wallpaperPath).toAbsolutePath().normalize().toUri().toString();
        String localWallpaperAbsolutePath = java.nio.file.Paths.get(localWallpaperPath).toAbsolutePath().normalize().toUri().toString();

        registry.addResourceHandler("/uploads/icon/custom/**")
                .addResourceLocations(iconAbsolutePath);

        registry.addResourceHandler("/uploads/bg_custom/**")
                .addResourceLocations(wallpaperAbsolutePath);

        registry.addResourceHandler("/uploads/sys_data/bg_img/**")
                .addResourceLocations(localWallpaperAbsolutePath);

        String sysIconAbsolutePath = java.nio.file.Paths.get(sysIconPath).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/icon/sys/**")
                .addResourceLocations(sysIconAbsolutePath);
    }
}
