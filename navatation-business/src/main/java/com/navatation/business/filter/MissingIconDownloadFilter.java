package com.navatation.business.filter;

import com.navatation.business.helper.FaviconFetcherHelper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * 拦截请求，解决因为跨设备、多数据源导致数据库存了图标路径但本地没有文件（404）的问题。
 * 当请求 /uploads/icon/sys/ 下的文件不存在时，自动触发重下载。
 */
import com.navatation.business.mapper.NavHomeShortcutMapper;
import com.navatation.business.mapper.RecommendHomeShortcutMapper;
import com.navatation.business.mapper.RecommendShortcutMapper;
import com.navatation.business.entity.nav.NavHomeShortcut;
import com.navatation.business.entity.recommend.RecommendHomeShortcut;
import com.navatation.business.entity.recommend.RecommendShortcut;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class MissingIconDownloadFilter implements Filter {

    @Value("${app.upload.sys-icon-path}")
    private String sysIconPath;

    private final FaviconFetcherHelper faviconFetcherHelper;
    private final NavHomeShortcutMapper navHomeShortcutMapper;
    private final RecommendHomeShortcutMapper recommendHomeShortcutMapper;
    private final RecommendShortcutMapper recommendShortcutMapper;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String uri = req.getRequestURI();

        if (uri != null && uri.startsWith("/uploads/icon/sys/")) {
            String filename = uri.substring("/uploads/icon/sys/".length());
            if (!filename.isEmpty() && !filename.contains("..")) {
                File targetDir = new File(sysIconPath);
                File file = new File(targetDir, filename);
                if (!file.exists()) {
                    log.debug("发现前端请求的系统图标文件不存在，触发异步重下载: {}", filename);
                    // 异步执行下载，不阻塞当前的 Tomcat 响应线程
                    java.util.concurrent.CompletableFuture.runAsync(() -> {
                        restoreMissingIcon(filename, file);
                    });
                    
                    // 立即重定向到 Google Favicon 兜底接口，彻底避免 404 且不阻塞线程
                    String host = getHostFromFilename(filename);
                    String fallbackUrl = "https://www.google.com/s2/favicons?domain=" + host + "&sz=128";
                    HttpServletResponse res = (HttpServletResponse) response;
                    res.sendRedirect(fallbackUrl);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    private String getHostFromFilename(String filename) {
        String host = filename;
        int underscoreIdx = filename.lastIndexOf('_');
        if (underscoreIdx > 0) {
            host = filename.substring(0, underscoreIdx);
        } else {
            int dotIdx = filename.lastIndexOf('.');
            if (dotIdx > 0) {
                host = filename.substring(0, dotIdx);
            }
        }
        return host;
    }

    private String restoreMissingIcon(String filename, File targetFile) {
        String host = getHostFromFilename(filename);

        try {
            // 使用 http 获取，FaviconFetcherHelper 内部会自动兜底和重定向
            String fetchUrl = "http://" + host;
            java.util.List<String> favs = faviconFetcherHelper.fetchFavicon(fetchUrl).getFaviconUrls();
            if (favs != null && !favs.isEmpty()) {
                String externalUrl = favs.get(0);
                String generatedPath = faviconFetcherHelper.downloadToLocal(externalUrl, host);
                
                // 由于 downloadToLocal 会保证每个域名只有一份最新的图标，
                // 我们直接更新数据库中的记录，让引用旧图标的地方改用新图标，而不是去拷贝复制文件！
                if (generatedPath != null && generatedPath.startsWith("/uploads/icon/sys/")) {
                    String oldIconValue = "/uploads/icon/sys/" + filename;
                    if (!generatedPath.equals(oldIconValue)) {
                        LambdaUpdateWrapper<NavHomeShortcut> uw1 = new LambdaUpdateWrapper<>();
                        uw1.eq(NavHomeShortcut::getIconValue, oldIconValue).set(NavHomeShortcut::getIconValue, generatedPath);
                        navHomeShortcutMapper.update(null, uw1);

                        LambdaUpdateWrapper<RecommendHomeShortcut> uw2 = new LambdaUpdateWrapper<>();
                        uw2.eq(RecommendHomeShortcut::getIconValue, oldIconValue).set(RecommendHomeShortcut::getIconValue, generatedPath);
                        recommendHomeShortcutMapper.update(null, uw2);

                        LambdaUpdateWrapper<RecommendShortcut> uw3 = new LambdaUpdateWrapper<>();
                        uw3.eq(RecommendShortcut::getIconValue, oldIconValue).set(RecommendShortcut::getIconValue, generatedPath);
                        recommendShortcutMapper.update(null, uw3);
                        
                        log.info("已将缺失图标对应的新图标路径更新至数据库，旧: {}, 新: {}", oldIconValue, generatedPath);
                    }
                    return null;
                } else {
                    return generatedPath;
                }
            }
        } catch (Exception e) {
            log.warn("主动重下载恢复图标失败 filename: {}, error: {}", filename, e.getMessage());
        }
        return null;
    }
}
