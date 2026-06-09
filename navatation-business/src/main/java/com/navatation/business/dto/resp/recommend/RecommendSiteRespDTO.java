package com.navatation.business.dto.resp.recommend;

import lombok.Data;
import com.navatation.business.dto.resp.recommend.RecommendSiteRespDTO;

/**
 * 推荐网址信息响应结果
 *
 * @date 2026-06-09
 */
@Data
public class RecommendSiteRespDTO {
    private String siteId;
    private String categoryId;
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;

}
