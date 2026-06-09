package com.navatation.business.dto.resp;

import lombok.Data;

@Data
public class RecommendSiteRespDTO {
    private String siteId;
    private String categoryId;
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;

}
