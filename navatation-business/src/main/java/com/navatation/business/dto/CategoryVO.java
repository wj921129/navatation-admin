package com.navatation.business.dto;

import lombok.Data;

@Data
public class CategoryVO {
    private String categoryId;
    private String name;
    private Double sortOrder;
    private Integer shortcutCount;
}
