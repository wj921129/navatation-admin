package com.navatation.business.dto.req;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

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
