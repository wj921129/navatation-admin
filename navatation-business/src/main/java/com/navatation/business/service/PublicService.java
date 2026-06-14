package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navatation.business.dto.resp.nav.CategoryRespDTO;
import com.navatation.business.dto.resp.nav.ShortcutRespDTO;
import com.navatation.business.dto.resp.widget.WidgetRespDTO;
import com.navatation.business.entity.recommend.RecommendConfig;
import com.navatation.business.mapper.RecommendConfigMapper;
import com.navatation.business.entity.user.User;
import com.navatation.business.mapper.UserMapper;
import com.navatation.business.dto.resp.settings.SettingsRespDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import com.navatation.business.entity.root.RootUser;
import com.navatation.business.mapper.RootUserMapper;

/**
 * PublicService 功能描述
 *
 * @date 2026-06-09
 */
@Service
@RequiredArgsConstructor
public class PublicService {

    private static final Logger log = LoggerFactory.getLogger(PublicService.class);

    private final UserMapper userMapper;
    private final RootUserMapper rootUserMapper;
    private final SettingsService settingsService;
    private final WidgetService widgetService;
    private final NavService navService;
    private final RecommendConfigMapper recommendConfigMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private String getAdminId() {
        RootUser admin = rootUserMapper.selectOne(new LambdaQueryWrapper<RootUser>().last("LIMIT 1"));
        if (admin == null) {
            log.warn("超级管理员账户不存在");
            return null;
        }
        return admin.getUserId();
    }

    public SettingsRespDTO getGuestSettings() {
        String adminId = getAdminId();
        if (adminId == null) return null;
        
        String cacheKey = "navatation:guest_config";
        try {
            Object cached = redisTemplate.opsForHash().get(cacheKey, "settings");
            if (cached != null) {
                return objectMapper.readValue((String) cached, SettingsRespDTO.class);
            }
            SettingsRespDTO settingsVO = settingsService.getSettings(adminId);
            redisTemplate.opsForHash().put(cacheKey, "settings", objectMapper.writeValueAsString(settingsVO));
            return settingsVO;
        } catch (Exception e) {
            log.error("游客配置Settings从 Redis 反序列化失败", e);
            redisTemplate.opsForHash().delete(cacheKey, "settings");
            return settingsService.getSettings(adminId);
        }
    }

    public List<WidgetRespDTO> getGuestWidgets() {
        String adminId = getAdminId();
        if (adminId == null) return List.of();
        
        String cacheKey = "navatation:guest_config";
        try {
            Object cached = redisTemplate.opsForHash().get(cacheKey, "widgets");
            if (cached != null) {
                return objectMapper.readValue((String) cached, new TypeReference<List<WidgetRespDTO>>() {});
            }
            List<WidgetRespDTO> widgetVOs = widgetService.getWidgets(adminId);
            redisTemplate.opsForHash().put(cacheKey, "widgets", objectMapper.writeValueAsString(widgetVOs));
            return widgetVOs;
        } catch (Exception e) {
            log.error("游客配置Widgets从 Redis 反序列化失败", e);
            redisTemplate.opsForHash().delete(cacheKey, "widgets");
            return widgetService.getWidgets(adminId);
        }
    }

    public List<CategoryRespDTO> getGuestCategories() {
        String adminId = getAdminId();
        if (adminId == null) return List.of();
        
        String cacheKey = "navatation:guest_config";
        try {
            Object cached = redisTemplate.opsForHash().get(cacheKey, "categories");
            if (cached != null) {
                return objectMapper.readValue((String) cached, new TypeReference<List<CategoryRespDTO>>() {});
            }
            List<CategoryRespDTO> categoryVOs = navService.getCategories(adminId);
            redisTemplate.opsForHash().put(cacheKey, "categories", objectMapper.writeValueAsString(categoryVOs));
            return categoryVOs;
        } catch (Exception e) {
            log.error("游客配置Categories从 Redis 反序列化失败", e);
            redisTemplate.opsForHash().delete(cacheKey, "categories");
            return navService.getCategories(adminId);
        }
    }

    public List<ShortcutRespDTO> getGuestShortcuts() {
        String adminId = getAdminId();
        if (adminId == null) return List.of();
        
        String cacheKey = "navatation:guest_config";
        try {
            Object cached = redisTemplate.opsForHash().get(cacheKey, "shortcuts");
            if (cached != null) {
                return objectMapper.readValue((String) cached, new TypeReference<List<ShortcutRespDTO>>() {});
            }
            List<ShortcutRespDTO> shortcutVOs = navService.getShortcuts(adminId, null);
            redisTemplate.opsForHash().put(cacheKey, "shortcuts", objectMapper.writeValueAsString(shortcutVOs));
            return shortcutVOs;
        } catch (Exception e) {
            log.error("游客配置Shortcuts从 Redis 反序列化失败", e);
            redisTemplate.opsForHash().delete(cacheKey, "shortcuts");
            return navService.getShortcuts(adminId, null);
        }
    }
}
