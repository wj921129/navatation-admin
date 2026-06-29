package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.navatation.business.dto.req.nav.HomeShortcutReqDTO;
import com.navatation.business.dto.resp.nav.HomeShortcutRespDTO;
import com.navatation.business.entity.nav.NavHomeShortcut;
import com.navatation.business.entity.recommend.RecommendHomeShortcut;
import com.navatation.business.entity.user.User;
import com.navatation.business.mapper.NavHomeShortcutMapper;
import com.navatation.business.mapper.RecommendHomeShortcutMapper;
import com.navatation.business.mapper.UserMapper;
import com.navatation.common.BizException;
import com.navatation.common.IdUtils;
import com.navatation.common.ResultCode;
import java.math.BigDecimal;
import com.navatation.business.helper.FaviconFetcherHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeShortcutService {

    private final NavHomeShortcutMapper navHomeShortcutMapper;
    private final RecommendHomeShortcutMapper recommendHomeShortcutMapper;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final FaviconFetcherHelper faviconFetcherHelper;

    @Qualifier("iconDownloadExecutor")
    private final Executor iconDownloadExecutor;

    private final java.util.Set<String> downloadingUrls = java.util.concurrent.ConcurrentHashMap.newKeySet();
    private final java.util.Map<String, Long> downloadFailRetryTime = new java.util.concurrent.ConcurrentHashMap<>();

    private boolean isAdmin(String userId) {
        User user = userMapper.selectById(userId);
        return user != null && "ADMIN".equals(user.getRole());
    }

    public List<HomeShortcutRespDTO> getHomeShortcuts(String userId) {
        if (isAdmin(userId)) {
            List<RecommendHomeShortcut> all = recommendHomeShortcutMapper.selectList(
                    new LambdaQueryWrapper<RecommendHomeShortcut>()
                            .orderByAsc(RecommendHomeShortcut::getSortOrder)
            );
            return buildNestedRecommendShortcuts(all);
        }

        List<NavHomeShortcut> all = navHomeShortcutMapper.selectList(
                new LambdaQueryWrapper<NavHomeShortcut>()
                        .eq(NavHomeShortcut::getUserId, userId)
                        .isNull(NavHomeShortcut::getCategoryId)
                        .orderByAsc(NavHomeShortcut::getSortOrder)
        );
        return buildNestedNavShortcuts(all);
    }

    private List<HomeShortcutRespDTO> buildNestedRecommendShortcuts(List<RecommendHomeShortcut> all) {
        Map<String, List<HomeShortcutRespDTO>> stackMap = all.stream()
                .filter(s -> s.getStackId() != null && !s.getStackId().isEmpty())
                .map(this::toVO)
                .collect(Collectors.groupingBy(HomeShortcutRespDTO::getStackId));

        return all.stream()
                .filter(s -> s.getStackId() == null || s.getStackId().isEmpty())
                .map(s -> {
                    HomeShortcutRespDTO vo = toVO(s);
                    if ("stack".equals(s.getType())) {
                        List<HomeShortcutRespDTO> children = stackMap.getOrDefault(s.getShortcutId(), new java.util.ArrayList<>());
                        vo.setChildren(children);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private List<HomeShortcutRespDTO> buildNestedNavShortcuts(List<NavHomeShortcut> all) {
        Map<String, List<HomeShortcutRespDTO>> stackMap = all.stream()
                .filter(s -> s.getStackId() != null && !s.getStackId().isEmpty())
                .map(this::toVO)
                .collect(Collectors.groupingBy(HomeShortcutRespDTO::getStackId));

        return all.stream()
                .filter(s -> s.getStackId() == null || s.getStackId().isEmpty())
                .map(s -> {
                    HomeShortcutRespDTO vo = toVO(s);
                    if ("stack".equals(s.getType())) {
                        List<HomeShortcutRespDTO> children = stackMap.getOrDefault(s.getShortcutId(), new java.util.ArrayList<>());
                        vo.setChildren(children);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }

    public HomeShortcutRespDTO addHomeShortcut(String userId, HomeShortcutReqDTO req) {
        if (isAdmin(userId)) {
            RecommendHomeShortcut hs = new RecommendHomeShortcut();
            hs.setShortcutId(IdUtils.genShortcutId()); // 借用普通的ID生成
            hs.setName(req.getName());
            hs.setUrl(req.getUrl());
            hs.setIconType(req.getIconType() != null ? req.getIconType() : "BUILTIN");
            hs.setIconValue(localizeIcon(hs.getIconType(), req.getIconValue(), req.getUrl()));
            hs.setIconColor(req.getIconColor());
            hs.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : BigDecimal.ZERO);
            recommendHomeShortcutMapper.insert(hs);
            redisTemplate.opsForHash().delete("navatation:guest_config", "home_shortcuts");
            return toVO(hs);
        }

        NavHomeShortcut hs = new NavHomeShortcut();
        hs.setShortcutId(IdUtils.genShortcutId());
        hs.setUserId(userId);
        hs.setName(req.getName());
        hs.setUrl(req.getUrl());
        hs.setIconType(req.getIconType() != null ? req.getIconType() : "BUILTIN");
        hs.setIconValue(localizeIcon(hs.getIconType(), req.getIconValue(), req.getUrl()));
        hs.setIconColor(req.getIconColor());
        hs.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : BigDecimal.ZERO);
        navHomeShortcutMapper.insert(hs);
        return toVO(hs);
    }

    public HomeShortcutRespDTO updateHomeShortcut(String userId, String shortcutId, HomeShortcutReqDTO req) {
        if (isAdmin(userId)) {
            RecommendHomeShortcut hs = recommendHomeShortcutMapper.selectById(shortcutId);
            if (hs == null) throw new BizException(ResultCode.NOT_FOUND);
            if (req.getName() != null) hs.setName(req.getName());
            if (req.getUrl() != null) hs.setUrl(req.getUrl());
            if (req.getIconType() != null) hs.setIconType(req.getIconType());
            if (req.getIconValue() != null) hs.setIconValue(req.getIconValue());
            
            hs.setIconValue(localizeIcon(hs.getIconType(), hs.getIconValue(), hs.getUrl()));

            if (req.getIconColor() != null) hs.setIconColor(req.getIconColor());
            if (req.getSortOrder() != null) hs.setSortOrder(req.getSortOrder());
            recommendHomeShortcutMapper.updateById(hs);
            redisTemplate.opsForHash().delete("navatation:guest_config", "home_shortcuts");
            return toVO(hs);
        }

        NavHomeShortcut hs = navHomeShortcutMapper.selectById(shortcutId);
        if (hs == null || !hs.getUserId().equals(userId)) throw new BizException(ResultCode.NOT_FOUND);
        if (req.getName() != null) hs.setName(req.getName());
        if (req.getUrl() != null) hs.setUrl(req.getUrl());
        if (req.getIconType() != null) hs.setIconType(req.getIconType());
        if (req.getIconValue() != null) hs.setIconValue(req.getIconValue());
        
        hs.setIconValue(localizeIcon(hs.getIconType(), hs.getIconValue(), hs.getUrl()));

        if (req.getIconColor() != null) hs.setIconColor(req.getIconColor());
        if (req.getSortOrder() != null) hs.setSortOrder(req.getSortOrder());
        navHomeShortcutMapper.updateById(hs);
        return toVO(hs);
    }

    public void deleteHomeShortcut(String userId, String shortcutId) {
        if (isAdmin(userId)) {
            RecommendHomeShortcut hs = recommendHomeShortcutMapper.selectById(shortcutId);
            if (hs == null) throw new BizException(ResultCode.NOT_FOUND);
            recommendHomeShortcutMapper.deleteById(shortcutId);
            redisTemplate.opsForHash().delete("navatation:guest_config", "home_shortcuts");
            return;
        }

        NavHomeShortcut hs = navHomeShortcutMapper.selectById(shortcutId);
        if (hs == null || !hs.getUserId().equals(userId)) throw new BizException(ResultCode.NOT_FOUND);
        navHomeShortcutMapper.deleteById(shortcutId);
    }

    public void batchSaveHomeShortcuts(String userId, List<HomeShortcutReqDTO> reqs) {
        if (reqs == null) {
            reqs = new java.util.ArrayList<>();
        }

        // 扁平化，保存原始顺序
        List<HomeShortcutReqDTO> flatReqs = new java.util.ArrayList<>();
        BigDecimal order = BigDecimal.ZERO;
        for (HomeShortcutReqDTO top : reqs) {
            top.setSortOrder(order);
            top.setStackId(null);
            if (top.getType() == null) {
                top.setType(top.getChildren() != null && !top.getChildren().isEmpty() ? "stack" : "single");
            }
            if ("stack".equals(top.getType()) && top.getShortcutId() == null) {
                top.setShortcutId(IdUtils.genShortcutId());
            }
            flatReqs.add(top);
            order = order.add(BigDecimal.ONE);
            
            if ("stack".equals(top.getType()) && top.getChildren() != null) {
                BigDecimal childOrder = BigDecimal.ZERO;
                for (HomeShortcutReqDTO child : top.getChildren()) {
                    child.setSortOrder(childOrder);
                    child.setStackId(top.getShortcutId());
                    child.setStackName(top.getName());
                    child.setType("single");
                    flatReqs.add(child);
                    childOrder = childOrder.add(BigDecimal.ONE);
                }
            }
        }

        if (isAdmin(userId)) {
            batchSaveRecommend(flatReqs);
            redisTemplate.opsForHash().delete("navatation:guest_config", "home_shortcuts");
            return;
        }

        batchSaveNav(userId, flatReqs);
    }

    private void batchSaveRecommend(List<HomeShortcutReqDTO> flatReqs) {
        List<RecommendHomeShortcut> existing = recommendHomeShortcutMapper.selectList(
            new LambdaQueryWrapper<RecommendHomeShortcut>()
        );
        Map<String, RecommendHomeShortcut> existingMap = existing.stream()
            .collect(Collectors.toMap(RecommendHomeShortcut::getShortcutId, x -> x));

        for (HomeShortcutReqDTO req : flatReqs) {
            String sid = req.getShortcutId();
            if (sid != null && existingMap.containsKey(sid)) {
                // Update
                RecommendHomeShortcut hs = existingMap.get(sid);
                hs.setName(req.getName());
                hs.setUrl(req.getUrl());
                hs.setIconType(req.getIconType());
                hs.setIconValue(localizeIcon(req.getIconType(), req.getIconValue(), req.getUrl()));
                hs.setIconColor(req.getIconColor());
                hs.setSortOrder(req.getSortOrder());
                hs.setType(req.getType());
                hs.setStackId(req.getStackId());
                hs.setStackName(req.getStackName());
                recommendHomeShortcutMapper.updateById(hs);
                existingMap.remove(sid);
            } else {
                // Insert
                RecommendHomeShortcut hs = new RecommendHomeShortcut();
                hs.setShortcutId(sid != null ? sid : IdUtils.genShortcutId());
                hs.setName(req.getName());
                hs.setUrl(req.getUrl());
                hs.setIconType(req.getIconType() != null ? req.getIconType() : "BUILTIN");
                hs.setIconValue(localizeIcon(hs.getIconType(), req.getIconValue(), req.getUrl()));
                hs.setIconColor(req.getIconColor());
                hs.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : BigDecimal.ZERO);
                hs.setType(req.getType() != null ? req.getType() : "single");
                hs.setStackId(req.getStackId());
                hs.setStackName(req.getStackName());
                recommendHomeShortcutMapper.insert(hs);
            }
        }

        // Delete removed
        for (String removeId : existingMap.keySet()) {
            recommendHomeShortcutMapper.deleteById(removeId);
        }
    }

    private void batchSaveNav(String userId, List<HomeShortcutReqDTO> flatReqs) {
        List<NavHomeShortcut> existing = navHomeShortcutMapper.selectList(
            new LambdaQueryWrapper<NavHomeShortcut>().eq(NavHomeShortcut::getUserId, userId).isNull(NavHomeShortcut::getCategoryId)
        );
        Map<String, NavHomeShortcut> existingMap = existing.stream()
            .collect(Collectors.toMap(NavHomeShortcut::getShortcutId, x -> x));

        for (HomeShortcutReqDTO req : flatReqs) {
            String sid = req.getShortcutId();
            if (sid != null && existingMap.containsKey(sid)) {
                // Update
                NavHomeShortcut hs = existingMap.get(sid);
                hs.setName(req.getName());
                hs.setUrl(req.getUrl());
                hs.setIconType(req.getIconType());
                hs.setIconValue(localizeIcon(req.getIconType(), req.getIconValue(), req.getUrl()));
                hs.setIconColor(req.getIconColor());
                hs.setSortOrder(req.getSortOrder());
                hs.setType(req.getType());
                hs.setStackId(req.getStackId());
                hs.setStackName(req.getStackName());
                navHomeShortcutMapper.updateById(hs);
                existingMap.remove(sid);
            } else {
                // Insert
                NavHomeShortcut hs = new NavHomeShortcut();
                hs.setShortcutId(sid != null ? sid : IdUtils.genShortcutId());
                hs.setUserId(userId);
                hs.setName(req.getName());
                hs.setUrl(req.getUrl());
                hs.setIconType(req.getIconType() != null ? req.getIconType() : "BUILTIN");
                hs.setIconValue(localizeIcon(hs.getIconType(), req.getIconValue(), req.getUrl()));
                hs.setIconColor(req.getIconColor());
                hs.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : BigDecimal.ZERO);
                hs.setType(req.getType() != null ? req.getType() : "single");
                hs.setStackId(req.getStackId());
                hs.setStackName(req.getStackName());
                navHomeShortcutMapper.insert(hs);
            }
        }

        // Delete removed
        for (String removeId : existingMap.keySet()) {
            navHomeShortcutMapper.deleteById(removeId);
        }
    }

    private HomeShortcutRespDTO toVO(RecommendHomeShortcut hs) {
        HomeShortcutRespDTO vo = new HomeShortcutRespDTO();
        vo.setShortcutId(hs.getShortcutId());
        vo.setName(hs.getName());
        vo.setUrl(hs.getUrl());
        vo.setIconType(hs.getIconType());
        vo.setIconColor(hs.getIconColor());
        vo.setSortOrder(hs.getSortOrder());
        vo.setType(hs.getType());
        vo.setStackId(hs.getStackId());
        vo.setStackName(hs.getStackName());

        String iconValue = hs.getIconValue();
        if ("FAVICON".equals(hs.getIconType()) && iconValue != null && (iconValue.startsWith("http://") || iconValue.startsWith("https://"))) {
            // 检查该 URL 是否在冷却退避中
            Long retryTime = downloadFailRetryTime.get(iconValue);
            if (retryTime != null && System.currentTimeMillis() < retryTime) {
                // 处于退避冷却期内，跳过下载
                vo.setIconValue(iconValue);
                return vo;
            }

            // 若可下载，通过 downloadingUrls.add(iconValue) 进行并发加锁
            if (downloadingUrls.add(iconValue)) {
                String finalIconValue = iconValue;
                iconDownloadExecutor.execute(() -> {
                    try {
                        String localVal = localizeIcon(hs.getIconType(), finalIconValue, hs.getUrl());
                        if (localVal != null && !localVal.equals(finalIconValue)) {
                            // 下载成功：更新数据库，清理游客缓存
                            hs.setIconValue(localVal);
                            recommendHomeShortcutMapper.updateById(hs);
                            redisTemplate.opsForHash().delete("navatation:guest_config", "home_shortcuts");
                            downloadFailRetryTime.remove(finalIconValue); // 清理失败冷却
                        } else {
                            // 未能成功本地化（如 downloadToLocal 返回原外部链接，视同下载失败）
                            downloadFailRetryTime.put(finalIconValue, System.currentTimeMillis() + 30 * 60 * 1000); // 冷却 30 分钟
                        }
                    } catch (Exception e) {
                        log.warn("异步自愈下载常用图标失败: {}, 冷却 30 分钟", e.getMessage());
                        downloadFailRetryTime.put(finalIconValue, System.currentTimeMillis() + 30 * 60 * 1000);
                    } finally {
                        downloadingUrls.remove(finalIconValue); // 释放下载锁
                    }
                });
            }
        }
        vo.setIconValue(iconValue);
        return vo;
    }

    private HomeShortcutRespDTO toVO(NavHomeShortcut hs) {
        HomeShortcutRespDTO vo = new HomeShortcutRespDTO();
        vo.setShortcutId(hs.getShortcutId());
        vo.setName(hs.getName());
        vo.setUrl(hs.getUrl());
        vo.setIconType(hs.getIconType());
        vo.setIconColor(hs.getIconColor());
        vo.setSortOrder(hs.getSortOrder());
        vo.setType(hs.getType());
        vo.setStackId(hs.getStackId());
        vo.setStackName(hs.getStackName());

        String iconValue = hs.getIconValue();
        if ("FAVICON".equals(hs.getIconType()) && iconValue != null && (iconValue.startsWith("http://") || iconValue.startsWith("https://"))) {
            // 检查该 URL 是否在冷却退避中
            Long retryTime = downloadFailRetryTime.get(iconValue);
            if (retryTime != null && System.currentTimeMillis() < retryTime) {
                // 处于退避冷却期内，跳过下载
                vo.setIconValue(iconValue);
                return vo;
            }

            // 若可下载，通过 downloadingUrls.add(iconValue) 进行并发加锁
            if (downloadingUrls.add(iconValue)) {
                String finalIconValue = iconValue;
                iconDownloadExecutor.execute(() -> {
                    try {
                        String localVal = localizeIcon(hs.getIconType(), finalIconValue, hs.getUrl());
                        if (localVal != null && !localVal.equals(finalIconValue)) {
                            // 下载成功：更新数据库
                            hs.setIconValue(localVal);
                            navHomeShortcutMapper.updateById(hs);
                            downloadFailRetryTime.remove(finalIconValue); // 清理失败冷却
                        } else {
                            // 未能成功本地化（如 downloadToLocal 返回原外部链接，视同下载失败）
                            downloadFailRetryTime.put(finalIconValue, System.currentTimeMillis() + 30 * 60 * 1000); // 冷却 30 分钟
                        }
                    } catch (Exception e) {
                        log.warn("异步自愈下载常用图标失败: {}, 冷却 30 分钟", e.getMessage());
                        downloadFailRetryTime.put(finalIconValue, System.currentTimeMillis() + 30 * 60 * 1000);
                    } finally {
                        downloadingUrls.remove(finalIconValue); // 释放下载锁
                    }
                });
            }
        }
        vo.setIconValue(iconValue);
        return vo;
    }

    private String localizeIcon(String iconType, String iconValue, String shortcutUrl) {
        if (!"FAVICON".equals(iconType) || iconValue == null || iconValue.isEmpty()) {
            return iconValue;
        }
        if (!iconValue.startsWith("http://") && !iconValue.startsWith("https://")) {
            return iconValue;
        }
        String host = null;
        try {
            if (shortcutUrl != null && !shortcutUrl.isEmpty()) {
                if (!shortcutUrl.startsWith("http://") && !shortcutUrl.startsWith("https://")) {
                    shortcutUrl = "http://" + shortcutUrl;
                }
                host = new java.net.URI(shortcutUrl).getHost();
            }
            if (host == null) {
                host = new java.net.URI(iconValue).getHost();
            }
        } catch (Exception e) {
            log.warn("解析 host 失败 shortcutUrl: {}, iconValue: {}", shortcutUrl, iconValue, e);
        }

        if (host == null || host.isEmpty()) {
            host = "unknown_host";
        }

        try {
            return faviconFetcherHelper.downloadToLocal(iconValue, host);
        } catch (Exception e) {
            log.warn("本地化图标异常 iconValue: {}, host: {}", iconValue, host, e);
            return iconValue;
        }
    }
}
