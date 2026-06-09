package com.navatation.business.dto.req;

import lombok.Data;

@Data
public class RecommendCategoryReqDTO {
    private String name;
    private String icon;
    private Double sortOrder;
}
