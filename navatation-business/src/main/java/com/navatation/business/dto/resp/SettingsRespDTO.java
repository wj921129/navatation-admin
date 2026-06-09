package com.navatation.business.dto.resp;

import lombok.Data;

@Data
public class SettingsRespDTO {
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
