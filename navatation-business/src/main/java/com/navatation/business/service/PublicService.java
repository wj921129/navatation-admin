package com.navatation.business.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.navatation.business.dto.CategoryVO;
import com.navatation.business.dto.GuestConfigVO;
import com.navatation.business.dto.ShortcutVO;
import com.navatation.business.dto.WidgetVO;
import com.navatation.business.entity.RecommendConfig;
import com.navatation.business.mapper.RecommendConfigMapper;
import com.navatation.business.entity.User;
import com.navatation.business.mapper.UserMapper;
import com.navatation.business.dto.SettingsVO;
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
    private final RecommendConfigMapper recommendConfigMapper;
    private final RecommendWidgetService recommendWidgetService;

    public GuestConfigVO getGuestConfig() {
        // Find ADMIN user
        User admin = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getRole, "ADMIN").last("LIMIT 1"));
        if (admin == null) {
            log.warn("超级管理员账户不存在，返回空游客配置");
            return new GuestConfigVO();
        }

        String adminId = admin.getUserId();
        GuestConfigVO vo = new GuestConfigVO();
        
        RecommendConfig rc = recommendConfigMapper.selectOne(new LambdaQueryWrapper<RecommendConfig>().last("LIMIT 1"));
        SettingsVO settingsVO = new SettingsVO();
        if (rc != null) {
            settingsVO.setSearchEngine(rc.getSearchEngine());
            settingsVO.setBackgroundImage(rc.getBackgroundImage());
            settingsVO.setBackgroundType(rc.getBackgroundType());
            settingsVO.setSearchBoxWidth(rc.getSearchBoxWidth());
            settingsVO.setSearchBoxHeight(rc.getSearchBoxHeight());
            settingsVO.setSearchBoxMarginTop(rc.getSearchBoxMarginTop());
            settingsVO.setIconSize(rc.getIconSize());
            settingsVO.setIconRadius(rc.getIconRadius());
            settingsVO.setIconSpacingX(rc.getIconSpacingX());
            settingsVO.setIconSpacingY(rc.getIconSpacingY());
            settingsVO.setIconTextGap(rc.getIconTextGap());
            settingsVO.setTextSize(rc.getTextSize());
            settingsVO.setIconsMarginTop(rc.getIconsMarginTop());
            settingsVO.setIconsMarginX(rc.getIconsMarginX());
            settingsVO.setTheme(rc.getTheme());
        }
        vo.setSettings(settingsVO);
        
        // 游客所有配置都与管理员首页样式同步，因此小组件直接使用管理员的组件配置
        List<WidgetVO> widgetVOs = widgetService.getWidgets(adminId);
        vo.setWidgets(widgetVOs);
        
        vo.setCategories(navService.getCategories(adminId));
        vo.setShortcuts(navService.getShortcuts(adminId, null));
        
        return vo;
    }
}
