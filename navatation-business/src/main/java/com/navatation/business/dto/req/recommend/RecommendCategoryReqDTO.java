package com.navatation.business.dto.req.recommend;

import lombok.Data;
import com.navatation.business.dto.req.recommend.RecommendCategoryReqDTO;

/**
 * RecommendCategoryReqDTO 功能描述
 *
 * @date 2026-06-09
 */
@Data
public class RecommendCategoryReqDTO {
    private String name;
    private String icon;
    private Double sortOrder;
}
