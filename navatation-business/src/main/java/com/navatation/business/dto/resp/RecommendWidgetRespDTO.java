package com.navatation.business.dto.resp;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class RecommendWidgetRespDTO {
    private String widgetId;
    private String widgetType;
    private String widgetStyle;
    private Map<String, Object> widgetData;
    private BigDecimal layoutX;
    private BigDecimal layoutY;
    private BigDecimal layoutW;
    private BigDecimal layoutH;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
