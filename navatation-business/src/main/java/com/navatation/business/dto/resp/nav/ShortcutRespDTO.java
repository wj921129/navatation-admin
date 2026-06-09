package com.navatation.business.dto.resp.nav;

import lombok.Data;
import com.navatation.business.dto.resp.nav.ShortcutRespDTO;

@Data
public class ShortcutRespDTO {
    private String shortcutId;
    private String categoryId;
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;
    private Double sortOrder;
    private String createdAt;
}
