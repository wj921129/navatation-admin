package com.navatation.business.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecommendCategoryVO {
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private List<RecommendSiteVO> sites;
}
