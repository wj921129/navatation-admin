package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.navatation.business.dto.CategoryVO;
import com.navatation.business.dto.GuestConfigVO;
import com.navatation.business.dto.ShortcutVO;
import com.navatation.business.dto.WidgetVO;
import com.navatation.business.entity.User;
import com.navatation.business.mapper.UserMapper;
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
    private final SettingsService settingsService;
    private final WidgetService widgetService;
    private final NavService navService;

    public GuestConfigVO getGuestConfig() {
        // Find ADMIN user
        User admin = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getRole, "ADMIN").last("LIMIT 1"));
        if (admin == null) {
            log.warn("超级管理员账户不存在，返回空游客配置");
            return new GuestConfigVO();
        }

        String adminId = admin.getUserId();
        GuestConfigVO vo = new GuestConfigVO();
        vo.setSettings(settingsService.getSettings(adminId));
        vo.setWidgets(widgetService.getWidgets(adminId));
        vo.setCategories(navService.getCategories(adminId));
        vo.setShortcuts(navService.getShortcuts(adminId, null));
        
        return vo;
    }
}
