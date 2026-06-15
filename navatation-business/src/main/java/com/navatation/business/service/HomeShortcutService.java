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
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeShortcutService {

    private final NavHomeShortcutMapper navHomeShortcutMapper;
    private final RecommendHomeShortcutMapper recommendHomeShortcutMapper;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

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
            hs.setIconValue(req.getIconValue());
            hs.setIconColor(req.getIconColor());
            hs.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0.0);
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
        hs.setIconValue(req.getIconValue());
        hs.setIconColor(req.getIconColor());
        hs.setSortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0.0);
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
        vo.setIconValue(hs.getIconValue());
        vo.setIconColor(hs.getIconColor());
        vo.setSortOrder(hs.getSortOrder());
        return vo;
    }

    private HomeShortcutRespDTO toVO(NavHomeShortcut hs) {
        HomeShortcutRespDTO vo = new HomeShortcutRespDTO();
        vo.setShortcutId(hs.getShortcutId());
        vo.setName(hs.getName());
        vo.setUrl(hs.getUrl());
        vo.setIconType(hs.getIconType());
        vo.setIconValue(hs.getIconValue());
        vo.setIconColor(hs.getIconColor());
        vo.setSortOrder(hs.getSortOrder());
        return vo;
    }
}
