package com.navatation.business.dto.req.recommend;

import lombok.Data;
import com.navatation.business.dto.req.recommend.RecommendCategoryReqDTO;

/**
 * 推荐分类操作请求参数
 *
 * @date 2026-06-09
 */
@Data
public class RecommendCategoryReqDTO {
    private String name;
    private String icon;
    private Double sortOrder;
}
