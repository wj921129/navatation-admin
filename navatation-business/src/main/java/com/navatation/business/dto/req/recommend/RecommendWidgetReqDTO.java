package com.navatation.business.dto.req.recommend;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 推荐组件操作请求参数
 *
 * @date 2026-06-09
 */
@Data
public class RecommendWidgetReqDTO {
    private String type;
    private String style;
    private Map<String, Object> meta;
    private BigDecimal x;
    private BigDecimal y;
}
