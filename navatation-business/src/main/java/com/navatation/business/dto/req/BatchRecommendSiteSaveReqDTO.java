package com.navatation.business.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * 推荐网址批量保存请求 DTO
 * 
 * - 包含指定分类下的最新完整推荐网站列表。
 * 
 * @author Antigravity
 * @date 2026-06-08
 */
@Data
public class BatchRecommendSiteSaveReqDTO {
    /** 待保存的推荐网址列表 */
    @NotNull(message = "推荐网址列表不能为空")
    @Valid
    private List<RecommendSiteItemDTO> sites;
}
