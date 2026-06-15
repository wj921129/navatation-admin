package com.navatation.business.dto.resp.nav;

import lombok.Data;

@Data
public class HomeShortcutRespDTO {
    private String shortcutId;
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;
    private Double sortOrder;
}
