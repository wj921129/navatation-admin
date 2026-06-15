package com.navatation.business.service;

import com.navatation.business.entity.recommend.RecommendShortcut;
import com.navatation.business.helper.FaviconFetcherHelper;
import com.navatation.business.mapper.RecommendShortcutMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class NavShortcutIconAsyncService {

    private final FaviconFetcherHelper faviconFetcherHelper;
    private final RecommendShortcutMapper recommendShortcutMapper;

    /**
     * 异步批量下载并在下载完成后悄悄更新数据库。
     *
     * @param pendingSites 等待下载的实体列表（要求必须已经落库并包含有效的 ID 和待下载的外部 url）
     */
    @Async("iconDownloadExecutor")
    public void asyncBatchDownloadAndSaveIcons(List<RecommendShortcut> pendingSites) {
        if (pendingSites == null || pendingSites.isEmpty()) {
            return;
        }
        
        long start = System.currentTimeMillis();
        log.info("开始异步并发下载 {} 个站点的图标...", pendingSites.size());

        // 使用 CompletableFuture 对所有站点进行并发处理
        List<CompletableFuture<Void>> futures = pendingSites.stream()
                .map(site -> CompletableFuture.runAsync(() -> {
                    String externalUrl = site.getIconValue();
                    try {
                        java.net.URI uri = new java.net.URI(externalUrl);
                        String host = uri.getHost();
                        if (host != null) {
                            String localPath = faviconFetcherHelper.downloadToLocal(externalUrl, host);
                            if (!externalUrl.equals(localPath)) {
                                // 如果返回了不同的本地路径，说明下载成功，更新数据库
                                RecommendShortcut updateEntity = new RecommendShortcut();
                                updateEntity.setShortcutId(site.getShortcutId());
                                updateEntity.setIconValue(localPath);
                                recommendShortcutMapper.updateById(updateEntity);
                                log.info("异步下载成功，站点 {} 图标更新为: {}", site.getName(), localPath);
                            } else {
                                log.warn("站点 {} 的图标下载失败或未改变: {}", site.getName(), externalUrl);
                            }
                        }
                    } catch (Exception e) {
                        log.warn("处理站点 {} 异步图标拉取失败 url: {}, error: {}", site.getName(), externalUrl, e.getMessage());
                    }
                }))
                .toList();

        // 阻塞等待当前这批次的所有并发下载任务都完成（不会阻塞主线程，因为整体在 @Async 线程中执行）
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        log.info("异步并发下载任务结束，共处理 {} 个，耗时 {} ms", pendingSites.size(), (System.currentTimeMillis() - start));
    }
}
