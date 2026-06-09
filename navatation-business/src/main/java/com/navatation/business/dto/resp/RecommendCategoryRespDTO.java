package com.navatation.business.dto.resp;

import lombok.Data;
import java.util.List;

@Data
public class RecommendCategoryRespDTO {
    private String categoryId;
    private String categoryName;
    private String categoryIcon;
    private Double sortOrder;
    private List<RecommendSiteRespDTO> sites;
}
