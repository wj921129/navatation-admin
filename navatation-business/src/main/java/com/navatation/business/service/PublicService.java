package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.navatation.business.dto.resp.CategoryRespDTO;
import com.navatation.business.dto.resp.GuestConfigRespDTO;
import com.navatation.business.dto.resp.ShortcutRespDTO;
import com.navatation.business.dto.resp.WidgetRespDTO;
import com.navatation.business.entity.recommend.RecommendConfig;
import com.navatation.business.mapper.RecommendConfigMapper;
import com.navatation.business.entity.user.User;
import com.navatation.business.mapper.UserMapper;
import com.navatation.business.dto.resp.SettingsRespDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicService {

    private static final Logger log = LoggerFactory.getLogger(PublicService.class);

    private final UserMapper userMapper;
    private final com.navatation.business.mapper.RootUserMapper rootUserMapper;
    private final SettingsService settingsService;
    private final WidgetService widgetService;
    private final NavService navService;
    private final RecommendConfigMapper recommendConfigMapper;
    private final RecommendWidgetService recommendWidgetService;

    public GuestConfigRespDTO getGuestConfig() {
        // Find ADMIN user
        com.navatation.business.entity.root.RootUser admin = rootUserMapper.selectOne(new LambdaQueryWrapper<com.navatation.business.entity.root.RootUser>().last("LIMIT 1"));
        if (admin == null) {
            log.warn("超级管理员账户不存在，返回空游客配置");
            return new GuestConfigRespDTO();
        }

        String adminId = admin.getUserId();
        GuestConfigRespDTO vo = new GuestConfigRespDTO();
        
        SettingsRespDTO settingsVO = settingsService.getSettings(adminId);
        vo.setSettings(settingsVO);
        
        // 游客所有配置都与管理员首页样式同步，因此小组件直接使用管理员的组件配置
        List<WidgetRespDTO> widgetVOs = widgetService.getWidgets(adminId);
        vo.setWidgets(widgetVOs);
        
        vo.setCategories(navService.getCategories(adminId));
        vo.setShortcuts(navService.getShortcuts(adminId, null));
        
        return vo;
    }
}
