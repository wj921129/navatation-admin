package com.navatation.business.dto.resp;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @Author admin
 * @CreateTime 2026-06-03
 * @Description 用户组件展示数据传输对象
 */
@Data
public class WidgetRespDTO {

    /**
     * 组件唯一ID
     */
    private String widgetId;

    /**
     * 组件类型
     */
    private String type;

    /**
     * 组件样式
     */
    private String style;

    /**
     * X轴百分比位置
     */
    private BigDecimal x;

    /**
     * Y轴百分比位置
     */
    private BigDecimal y;

    /**
     * 元数据字典
     */
    private Map<String, Object> meta;
}
