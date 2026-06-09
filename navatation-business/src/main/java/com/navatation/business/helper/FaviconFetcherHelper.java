package com.navatation.business.helper;

import com.navatation.business.dto.resp.nav.FaviconRespDTO;
import com.navatation.common.BizException;
import com.navatation.common.RedisConstants;
import com.navatation.common.ResultCode;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import com.navatation.common.NavConstants;

/**
 * FaviconFetcherHelper 功能描述
 *
 * @date 2026-06-09
 */
@Component
@RequiredArgsConstructor
public class FaviconFetcherHelper {

    private static final Logger log = LoggerFactory.getLogger(FaviconFetcherHelper.class);

    private final RedisTemplate<String, Object> redisTemplate;

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
     * fetchFavicon 方法
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
                String cachedUrl = (String) redisTemplate.opsForValue().get(cacheKey);
                if (cachedUrl != null) {
                    log.info("Favicon 缓存命中 host: {} -> {}", host, cachedUrl);
                    FaviconRespDTO vo = new FaviconRespDTO();
                    vo.setFaviconUrl(cachedUrl);
                    vo.setSourceUrl(url);
                    return vo;
                }
            } catch (Exception e) {
                log.warn("从 Redis 读取 Favicon 缓存失败: {}", e.getMessage());
            }

            String faviconUrl = tryExtractFromHtml(url, scheme, host);
            if (faviconUrl == null) {
                faviconUrl = scheme + "://" + host + "/favicon.ico";
            }

            try {
                redisTemplate.opsForValue().set(cacheKey, faviconUrl, 7, TimeUnit.DAYS);
            } catch (Exception e) {
                log.warn("写入 Redis Favicon 缓存失败: {}", e.getMessage());
            }

            FaviconRespDTO vo = new FaviconRespDTO();
            vo.setFaviconUrl(faviconUrl);
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
                            fallbackVo.setFaviconUrl(scheme + "://" + host + "/favicon.ico");
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
                        fallback.setFaviconUrl(scheme + "://" + host + "/favicon.ico");
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

    private String tryExtractFromHtml(String pageUrl, String scheme, String host) {
        try {
            Document doc = Jsoup.connect(pageUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .referrer("https://www.google.com")
                    .timeout(5000)
                    .followRedirects(true)
                    .get();

            Element iconElement = doc.selectFirst("link[rel~=(?i)^(apple-touch-icon|apple-touch-icon-precomposed)$]");
            if (iconElement == null) {
                iconElement = doc.selectFirst("link[rel~=(?i)^(shortcut )?icon$]");
            }

            if (iconElement != null) {
                String href = iconElement.attr("href");
                if (href != null && !href.isEmpty()) {
                    return resolveFaviconUrl(href, scheme, host);
                }
            }
        } catch (Exception e) {
            log.warn("从页面 {} 用 Jsoup 解析 Favicon 失败: {}", pageUrl, e.getMessage());
        }
        return null;
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
}
