package com.navatation.business.dto.req.widget;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;
import com.navatation.business.dto.req.widget.WidgetReqDTO;

/**
 * @Author admin
 * @CreateTime 2026-06-03
 * @Description 用户组件保存请求传输对象
 */
@Data
public class WidgetReqDTO {

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
