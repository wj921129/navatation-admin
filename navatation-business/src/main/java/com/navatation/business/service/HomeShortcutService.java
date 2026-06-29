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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
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

    private boolean isAdmin(String userId) {
        User user = userMapper.selectById(userId);
        return user != null && "ADMIN".equals(user.getRole());
    }

    public List<HomeShortcutRespDTO> getHomeShortcuts(String userId) {
        if (isAdmin(userId)) {
            return recommendHomeShortcutMapper.selectList(
                    new LambdaQueryWrapper<RecommendHomeShortcut>()
                            .orderByAsc(RecommendHomeShortcut::getSortOrder)
            ).stream().map(this::toVO).collect(Collectors.toList());
        }

        return navHomeShortcutMapper.selectList(
                new LambdaQueryWrapper<NavHomeShortcut>()
                        .eq(NavHomeShortcut::getUserId, userId)
                        .isNull(NavHomeShortcut::getCategoryId)
                        .orderByAsc(NavHomeShortcut::getSortOrder)
        ).stream().map(this::toVO).collect(Collectors.toList());
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

    private HomeShortcutRespDTO toVO(RecommendHomeShortcut hs) {
        HomeShortcutRespDTO vo = new HomeShortcutRespDTO();
        vo.setShortcutId(hs.getShortcutId());
        vo.setName(hs.getName());
        vo.setUrl(hs.getUrl());
        vo.setIconType(hs.getIconType());
        vo.setIconColor(hs.getIconColor());
        vo.setSortOrder(hs.getSortOrder());

        String iconValue = hs.getIconValue();
        if ("FAVICON".equals(hs.getIconType()) && iconValue != null && (iconValue.startsWith("http://") || iconValue.startsWith("https://"))) {
            String localPath = localizeIcon(hs.getIconType(), iconValue, hs.getUrl());
            if (!iconValue.equals(localPath)) {
                hs.setIconValue(localPath);
                recommendHomeShortcutMapper.updateById(hs);
                redisTemplate.opsForHash().delete("navatation:guest_config", "home_shortcuts");
                iconValue = localPath;
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

        String iconValue = hs.getIconValue();
        if ("FAVICON".equals(hs.getIconType()) && iconValue != null && (iconValue.startsWith("http://") || iconValue.startsWith("https://"))) {
            String localPath = localizeIcon(hs.getIconType(), iconValue, hs.getUrl());
            if (!iconValue.equals(localPath)) {
                hs.setIconValue(localPath);
                navHomeShortcutMapper.updateById(hs);
                iconValue = localPath;
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
