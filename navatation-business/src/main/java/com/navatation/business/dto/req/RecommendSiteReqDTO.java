package com.navatation.business.dto.req;

import lombok.Data;

@Data
public class RecommendSiteReqDTO {
    private String categoryId;
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;

}
