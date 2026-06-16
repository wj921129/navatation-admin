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
@Slf4j
@Component
@RequiredArgsConstructor
public class MissingIconDownloadFilter implements Filter {

    @Value("${app.upload.sys-icon-path}")
    private String sysIconPath;

    private final FaviconFetcherHelper faviconFetcherHelper;

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
                    
                    // 立即返回 404（或者交给后续的静态资源处理器返回 404），
                    // 这样前端会显示兜底图标，后端后台慢慢下载，下次刷新时即可正常显示。
                    chain.doFilter(request, response);
                    return;
                }
            }
        }
        chain.doFilter(request, response);
    }

    private String restoreMissingIcon(String filename, File targetFile) {
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

        try {
            // 使用 http 获取，FaviconFetcherHelper 内部会自动兜底和重定向
            String fetchUrl = "http://" + host;
            java.util.List<String> favs = faviconFetcherHelper.fetchFavicon(fetchUrl).getFaviconUrls();
            if (favs != null && !favs.isEmpty()) {
                String externalUrl = favs.get(0);
                String generatedPath = faviconFetcherHelper.downloadToLocal(externalUrl, host);
                
                // 由于 downloadToLocal 可能生成基于新哈希规则或扩展名的文件，
                // 如果生成的文件名跟请求的缺失文件名不一致，我们将它拷贝一份到缺失的文件名上，
                // 确保前端该次请求以及历史数据库能够命中该文件。
                if (generatedPath != null && generatedPath.startsWith("/uploads/icon/sys/")) {
                    String genFileName = generatedPath.substring("/uploads/icon/sys/".length());
                    File genFile = new File(targetFile.getParentFile(), genFileName);
                    if (genFile.exists() && !genFile.getAbsolutePath().equals(targetFile.getAbsolutePath())) {
                        java.nio.file.Files.copy(genFile.toPath(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        log.info("已将重下载的图标 {} 拷贝恢复为缺失文件 {}", genFileName, filename);
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
