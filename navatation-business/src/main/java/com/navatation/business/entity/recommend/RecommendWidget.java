package com.navatation.business.entity.recommend;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author wanggy
 * @CreateTime 2026-06-08
 * @Description 推荐小组件实体
 */
@Data
@TableName("navatation_recommend_widget")
public class RecommendWidget {
    /** 物理主键 */
    private Long rowId;
    
    /** 业务主键 */
    @TableId(type = IdType.INPUT)
    private String widgetId;
    
    /** 组件类型 */
    private String widgetType;
    
    /** 组件样式 */
    private String widgetStyle;
    
    /** 组件数据 */
    private String widgetData;
    
    /** 布局X坐标 */
    private BigDecimal layoutX;
    
    /** 布局Y坐标 */
    private BigDecimal layoutY;
    
    /** 布局宽度 */
    private BigDecimal layoutW;
    
    /** 布局高度 */
    private BigDecimal layoutH;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
