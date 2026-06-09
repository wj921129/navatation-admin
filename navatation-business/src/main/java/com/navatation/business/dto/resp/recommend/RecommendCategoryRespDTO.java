package com.navatation.business.dto.resp.recommend;

import lombok.Data;
import java.util.List;
import com.navatation.business.dto.resp.recommend.RecommendSiteRespDTO;
import com.navatation.business.dto.resp.recommend.RecommendCategoryRespDTO;

/**
 * RecommendCategoryRespDTO 功能描述
 *
 * @date 2026-06-09
 */
@Data
public class RecommendCategoryRespDTO {
    private String categoryId;
    private String categoryName;
    private String categoryIcon;
    private Double sortOrder;
    private List<RecommendSiteRespDTO> sites;
}
