package com.navatation.business.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class RecommendWidgetRequest {
    private String widgetType;
    private String widgetStyle;
    private Map<String, Object> widgetData;
    private BigDecimal layoutX;
    private BigDecimal layoutY;
    private BigDecimal layoutW;
    private BigDecimal layoutH;
}
