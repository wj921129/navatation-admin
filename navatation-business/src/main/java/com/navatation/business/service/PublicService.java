package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    private final RecommendWidgetService recommendWidgetService;

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
