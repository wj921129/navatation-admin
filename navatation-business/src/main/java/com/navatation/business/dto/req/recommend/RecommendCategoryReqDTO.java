package com.navatation.business.dto.req.recommend;

import lombok.Data;
import com.navatation.business.dto.req.recommend.RecommendCategoryReqDTO;

@Data
public class RecommendCategoryReqDTO {
    private String name;
    private String icon;
    private Double sortOrder;
}
