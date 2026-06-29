package com.navatation.business.dto.resp.nav;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HomeShortcutRespDTO {
    private String shortcutId;
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;
    private BigDecimal sortOrder;
    private String type;
    private String stackId;
    private String stackName;
    private java.util.List<HomeShortcutRespDTO> children;
}
