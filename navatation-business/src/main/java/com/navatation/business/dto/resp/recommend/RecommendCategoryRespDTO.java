package com.navatation.business.dto.resp.recommend;

import lombok.Data;
import java.util.List;
import com.navatation.business.dto.resp.recommend.RecommendSiteRespDTO;
import com.navatation.business.dto.resp.recommend.RecommendCategoryRespDTO;

/**
 * 推荐分类信息响应结果
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
