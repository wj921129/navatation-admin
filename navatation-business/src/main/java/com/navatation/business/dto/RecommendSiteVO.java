package com.navatation.business.dto;

import lombok.Data;

@Data
public class RecommendSiteVO {
    private String siteId;
    private String categoryId;
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;
    private Double sortOrder;
}
