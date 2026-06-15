package com.navatation.business.dto.resp.recommend;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 推荐组件响应参数
 */
@Data
public class RecommendWidgetRespDTO {
    private String widgetId;
    private String type;
    private String style;
    private Map<String, Object> meta;
    private BigDecimal x;
    private BigDecimal y;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
