package com.navatation.business.dto.req.recommend;

import lombok.Data;
import com.navatation.business.dto.req.recommend.RecommendSiteReqDTO;

/**
 * 推荐网址操作请求参数
 *
 * @date 2026-06-09
 */
@Data
public class RecommendSiteReqDTO {
    private String categoryId;
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;

}
