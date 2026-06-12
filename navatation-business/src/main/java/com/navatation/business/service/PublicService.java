package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navatation.business.dto.resp.nav.CategoryRespDTO;
import com.navatation.business.dto.resp.settings.GuestConfigRespDTO;
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

    /**
     * getGuestConfig 方法
     */
    public GuestConfigRespDTO getGuestConfig() {
        // Find ADMIN user
        RootUser admin = rootUserMapper.selectOne(new LambdaQueryWrapper<RootUser>().last("LIMIT 1"));
        if (admin == null) {
            log.warn("超级管理员账户不存在，返回空游客配置");
            return new GuestConfigRespDTO();
        }

        String adminId = admin.getUserId();
        String cacheKey = "navatation:guest_config";
        GuestConfigRespDTO vo = new GuestConfigRespDTO();

        try {
            // 获取 Hash 中所有的值
            List<Object> hashValues = redisTemplate.opsForHash().multiGet(cacheKey, List.of("settings", "widgets", "categories", "shortcuts"));
            
            // 1. Settings
            if (hashValues.get(0) != null) {
                vo.setSettings(objectMapper.readValue((String) hashValues.get(0), SettingsRespDTO.class));
            } else {
                SettingsRespDTO settingsVO = settingsService.getSettings(adminId);
                vo.setSettings(settingsVO);
                redisTemplate.opsForHash().put(cacheKey, "settings", objectMapper.writeValueAsString(settingsVO));
            }

            // 2. Widgets
            if (hashValues.get(1) != null) {
                vo.setWidgets(objectMapper.readValue((String) hashValues.get(1), new TypeReference<List<WidgetRespDTO>>() {}));
            } else {
                List<WidgetRespDTO> widgetVOs = widgetService.getWidgets(adminId);
                vo.setWidgets(widgetVOs);
                redisTemplate.opsForHash().put(cacheKey, "widgets", objectMapper.writeValueAsString(widgetVOs));
            }

            // 3. Categories
            if (hashValues.get(2) != null) {
                vo.setCategories(objectMapper.readValue((String) hashValues.get(2), new TypeReference<List<CategoryRespDTO>>() {}));
            } else {
                List<CategoryRespDTO> categoryVOs = navService.getCategories(adminId);
                vo.setCategories(categoryVOs);
                redisTemplate.opsForHash().put(cacheKey, "categories", objectMapper.writeValueAsString(categoryVOs));
            }

            // 4. Shortcuts
            if (hashValues.get(3) != null) {
                vo.setShortcuts(objectMapper.readValue((String) hashValues.get(3), new TypeReference<List<ShortcutRespDTO>>() {}));
            } else {
                List<ShortcutRespDTO> shortcutVOs = navService.getShortcuts(adminId, null);
                vo.setShortcuts(shortcutVOs);
                redisTemplate.opsForHash().put(cacheKey, "shortcuts", objectMapper.writeValueAsString(shortcutVOs));
            }
        } catch (Exception e) {
            log.error("游客配置从 Redis 反序列化失败", e);
            redisTemplate.delete(cacheKey);
            // 降级：重新查询但不缓存，避免死循环
            vo.setSettings(settingsService.getSettings(adminId));
            vo.setWidgets(widgetService.getWidgets(adminId));
            vo.setCategories(navService.getCategories(adminId));
            vo.setShortcuts(navService.getShortcuts(adminId, null));
        }

        return vo;
    }
}
