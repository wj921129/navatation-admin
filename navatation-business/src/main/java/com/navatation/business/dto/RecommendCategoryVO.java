package com.navatation.business.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecommendCategoryVO {
    private String categoryId;
    private String categoryName;
    private String categoryIcon;
    private Integer sortOrder;
    private List<RecommendSiteVO> sites;
}
