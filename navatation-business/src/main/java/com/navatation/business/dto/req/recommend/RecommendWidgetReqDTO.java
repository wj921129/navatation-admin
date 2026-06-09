package com.navatation.business.dto.req.recommend;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;
import com.navatation.business.dto.req.recommend.RecommendWidgetReqDTO;

/**
 * 推荐组件操作请求参数
 *
 * @date 2026-06-09
 */
@Data
public class RecommendWidgetReqDTO {
    private String widgetType;
    private String widgetStyle;
    private Map<String, Object> widgetData;
    private BigDecimal layoutX;
    private BigDecimal layoutY;
    private BigDecimal layoutW;
    private BigDecimal layoutH;
}
