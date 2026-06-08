package com.navatation.business.dto;

import lombok.Data;

@Data
public class RecommendCategoryRequest {
    private String name;
    private String icon;
    private Integer sortOrder;
}
