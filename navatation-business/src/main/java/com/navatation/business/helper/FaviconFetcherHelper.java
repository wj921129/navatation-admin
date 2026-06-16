package com.navatation.business.helper;

import com.navatation.business.dto.resp.nav.FaviconRespDTO;
import com.navatation.common.BizException;
import com.navatation.common.RedisConstants;
import com.navatation.common.ResultCode;
import com.navatation.common.NavConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Favicon 抓取与本地化工具
 * 支持 HTML 多图标解析、Redis 缓存、外部图标下载到本地
 *
 * @date 2026-06-09
 */
@Component
@RequiredArgsConstructor
public class FaviconFetcherHelper {

    private static final Logger log = LoggerFactory.getLogger(FaviconFetcherHelper.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.upload.sys-icon-path}")
    private String sysIconPath;

    private final ExecutorService faviconExecutor = new ThreadPoolExecutor(
            5,
            10,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(200),
            new ThreadFactory() {
                private final AtomicInteger count = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "favicon-crawler-" + count.incrementAndGet());
                    t.setDaemon(true);
                    return t;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 抓取指定 URL 的网站图标，返回多个候选 URL 列表
     */
    public FaviconRespDTO fetchFavicon(String url) {
        try {
            java.net.URI uri = new java.net.URI(url);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (scheme == null || host == null) {
                throw new BizException(ResultCode.BAD_REQUEST);
            }

            String cacheKey = RedisConstants.KEY_NAV_FAVICON + host;
            try {
                String cachedJson = (String) redisTemplate.opsForValue().get(cacheKey);
                if (cachedJson != null) {
                    log.info("Favicon 缓存命中 host: {}", host);
                    List<String> cachedUrls = MAPPER.readValue(cachedJson, new TypeReference<List<String>>() {});
                    FaviconRespDTO vo = new FaviconRespDTO();
                    vo.setFaviconUrls(cachedUrls);
                    vo.setSourceUrl(url);
                    return vo;
                }
            } catch (Exception e) {
                log.warn("从 Redis 读取 Favicon 缓存失败: {}", e.getMessage());
            }

            List<String> faviconUrls = tryExtractAllFromHtml(url, scheme, host);
            if (faviconUrls.isEmpty()) {
                faviconUrls.add(scheme + "://" + host + "/favicon.ico");
            }

            // 过滤掉 404 等无效链接
            List<String> validUrls = faviconUrls.stream()
                    .filter(this::isValidFavicon)
                    .collect(Collectors.toList());

            // 如果全部无效（例如原站图标 404），重启搜索功能：使用第三方服务兜底
            if (validUrls.isEmpty()) {
                validUrls.add("https://www.google.com/s2/favicons?domain=" + host + "&sz=128");
            }

            // 直接返回原外链，不再自动下载，留给保存动作触发下载
            try {
                redisTemplate.opsForValue().set(cacheKey, MAPPER.writeValueAsString(validUrls), 7, TimeUnit.DAYS);
            } catch (Exception e) {
                log.warn("写入 Redis Favicon 缓存失败: {}", e.getMessage());
            }

            FaviconRespDTO vo = new FaviconRespDTO();
            vo.setFaviconUrls(validUrls);
            vo.setSourceUrl(url);
            return vo;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(ResultCode.BAD_REQUEST);
        }
    }

    public Map<String, FaviconRespDTO> fetchFaviconsInBatch(List<String> urls) {
        if (urls == null || urls.isEmpty()) {
            return new ConcurrentHashMap<>();
        }
        if (urls.size() > NavConstants.MAX_BATCH_FAVICON_SIZE) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "单次批量刷新最多支持100个网址");
        }

        List<String> uniqueUrls = urls.stream()
                .filter(u -> u != null && !u.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());

        Map<String, FaviconRespDTO> results = new ConcurrentHashMap<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String url : uniqueUrls) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    FaviconRespDTO vo = fetchFavicon(url);
                    results.put(url, vo);
                } catch (Exception e) {
                    log.warn("批量抓取单个 Favicon 异常 url: {}, error: {}", url, e.getMessage());
                    FaviconRespDTO fallbackVo = new FaviconRespDTO();
                    fallbackVo.setSourceUrl(url);
                    try {
                        java.net.URI uri = new java.net.URI(url);
                        String scheme = uri.getScheme();
                        String host = uri.getHost();
                        if (scheme != null && host != null) {
                            fallbackVo.setFaviconUrls(List.of("https://www.google.com/s2/favicons?domain=" + host + "&sz=128"));
                        }
                    } catch (Exception ex) {
                        // ignore
                    }
                    results.put(url, fallbackVo);
                }
            }, faviconExecutor);
            futures.add(future);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(15, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log.warn("批量抓取 Favicon 部分任务执行超时，已自动截断");
        } catch (Exception e) {
            log.error("批量抓取 Favicon 时发生并发等待异常", e);
        }

        for (String url : uniqueUrls) {
            results.computeIfAbsent(url, u -> {
                FaviconRespDTO fallback = new FaviconRespDTO();
                fallback.setSourceUrl(u);
                try {
                    java.net.URI uri = new java.net.URI(u);
                    String scheme = uri.getScheme();
                    String host = uri.getHost();
                    if (scheme != null && host != null) {
                        fallback.setFaviconUrls(List.of("https://www.google.com/s2/favicons?domain=" + host + "&sz=128"));
                    }
                } catch (Exception e) {
                    // ignore
                }
                return fallback;
            });
        }

        return results;
    }

    /**
     * 将外部图标 URL 下载到本地 sys-icon-path 目录。
     * 文件已存在则跳过（幂等）。
     *
     * @param externalUrl 外部图标 URL
     * @param host 目标网站 host，作为文件名
     * @return 本地相对路径（如 /uploads/icon/sys/youtube.com.png），失败时返回原始 externalUrl
     */
    public String downloadToLocal(String externalUrl, String host) {
        if (externalUrl == null || host == null) {
            return externalUrl;
        }
        // 已是本地路径则直接返回
        if (externalUrl.startsWith("/uploads/icon/sys/")) {
            return externalUrl;
        }
        try {
            String ext = guessExtension(externalUrl);
            String hash = org.springframework.util.DigestUtils.md5DigestAsHex(host.getBytes()).substring(0, 8);
            String fileName = host + "_" + hash + "." + ext;
            java.io.File targetDir = new java.io.File(sysIconPath);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            java.io.File targetFile = new java.io.File(targetDir, fileName);
            if (targetFile.exists()) {
                log.info("系统图标已存在，跳过下载: {}", fileName);
                return "/uploads/icon/sys/" + fileName;
            }

            java.net.URL url = new java.net.URL(externalUrl);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            conn.setInstanceFollowRedirects(true);

            int status = conn.getResponseCode();
            if (status != 200) {
                log.warn("下载系统图标失败 HTTP {} url: {}", status, externalUrl);
                return externalUrl;
            }

            String contentType = conn.getContentType();
            String betterExt = extensionFromContentType(contentType);
            if (betterExt != null) {
                fileName = host + "_" + hash + "." + betterExt;
                targetFile = new java.io.File(targetDir, fileName);
                if (targetFile.exists()) {
                    return "/uploads/icon/sys/" + fileName;
                }
            }

            try (java.io.InputStream in = conn.getInputStream()) {
                java.nio.file.Files.copy(in, targetFile.toPath());
            }
            log.info("系统图标下载成功: {} -> {}", host, fileName);
            return "/uploads/icon/sys/" + fileName;
        } catch (Exception e) {
            log.warn("下载系统图标异常 url: {}, error: {}", externalUrl, e.getMessage());
            return externalUrl;
        }
    }

    /**
     * destroyExecutor 方法
     */
    @PreDestroy
    public void destroyExecutor() {
        log.info("正在关闭 FaviconFetcherHelper 批量抓取 Favicon 线程池...");
        faviconExecutor.shutdown();
        try {
            if (!faviconExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                faviconExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            faviconExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("FaviconFetcherHelper 批量抓取 Favicon 线程池已安全关闭。");
    }

    /**
     * 解析目标页面 HTML 中所有图标引用，返回去重后的 URL 列表
     */
    private List<String> tryExtractAllFromHtml(String pageUrl, String scheme, String host) {
        List<String> urls = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(pageUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .referrer("https://www.google.com")
                    .timeout(5000)
                    .followRedirects(true)
                    .get();

            // 覆盖所有图标类型：apple-touch-icon、icon、shortcut icon
            Elements iconElements = doc.select(
                    "link[rel~=(?i)^(apple-touch-icon|apple-touch-icon-precomposed|icon|shortcut icon)$]");

            for (Element el : iconElements) {
                String href = el.attr("href");
                if (href != null && !href.isEmpty()) {
                    String resolved = resolveFaviconUrl(href, scheme, host);
                    if (!urls.contains(resolved)) {
                        urls.add(resolved);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("从页面 {} 用 Jsoup 解析全部 Favicon 失败: {}", pageUrl, e.getMessage());
        }
        return urls;
    }

    private static String resolveFaviconUrl(String href, String scheme, String host) {
        if (href.startsWith("http://") || href.startsWith("https://")) {
            return href;
        }
        if (href.startsWith("//")) {
            return scheme + ":" + href;
        }
        String base = scheme + "://" + host;
        return href.startsWith("/") ? base + href : base + "/" + href;
    }

    private String guessExtension(String url) {
        String lower = url.toLowerCase();
        if (lower.contains(".svg")) return "svg";
        if (lower.contains(".ico")) return "ico";
        if (lower.contains(".gif")) return "gif";
        if (lower.contains(".jpg") || lower.contains(".jpeg")) return "jpg";
        return "png";
    }

    private String extensionFromContentType(String contentType) {
        if (contentType == null) return null;
        if (contentType.contains("svg")) return "svg";
        if (contentType.contains("icon") || contentType.contains("ico")) return "ico";
        if (contentType.contains("gif")) return "gif";
        if (contentType.contains("jpeg")) return "jpg";
        if (contentType.contains("png")) return "png";
        return null;
    }

    /**
     * 发送轻量请求检测图标链接是否存活（过滤 404 等无效链接）
     */
    private boolean isValidFavicon(String urlStr) {
        if (urlStr == null || urlStr.isEmpty()) return false;
        if (!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) return true;
        try {
            java.net.URL url = new java.net.URL(urlStr);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            int status = conn.getResponseCode();

            // 部分服务器可能禁用 HEAD 请求，降级尝试 GET
            if (status == 405 || status == 403) {
                conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                status = conn.getResponseCode();
            }
            return status >= 200 && status < 400;
        } catch (Exception e) {
            return false; // 请求超时或域名解析失败等，视为无效
        }
    }
}
