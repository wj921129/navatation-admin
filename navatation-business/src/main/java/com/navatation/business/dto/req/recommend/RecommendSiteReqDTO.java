package com.navatation.business.dto.req.recommend;

import lombok.Data;
import com.navatation.business.dto.req.recommend.RecommendSiteReqDTO;

@Data
public class RecommendSiteReqDTO {
    private String categoryId;
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;

}
