package com.navatation.business.dto;

import lombok.Data;

@Data
public class ShortcutVO {
    private String shortcutId;
    private String categoryId;
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;
    private Integer sortOrder;
    private String createdAt;
}
