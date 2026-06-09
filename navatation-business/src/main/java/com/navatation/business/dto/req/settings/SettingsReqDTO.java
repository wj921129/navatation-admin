package com.navatation.business.dto.req.settings;

import lombok.Data;
import com.navatation.business.dto.req.settings.SettingsReqDTO;

/**
 * 用户设置修改请求参数
 *
 * @date 2026-06-09
 */
@Data
public class SettingsReqDTO {
    private String searchEngine;
    private String backgroundImage;
    private String backgroundType;
    private Integer searchBoxWidth;
    private Integer searchBoxHeight;
    private Integer searchBoxMarginTop;
    private Integer iconSize;
    private Integer iconRadius;
    private Integer iconSpacingX;
    private Integer iconSpacingY;
    private Integer iconTextGap;
    private Integer textSize;
    private Integer iconsMarginTop;
    private Integer iconsMarginX;
    private String theme;
}
