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
 * 推荐小组件实体
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
    private String type;
    
    /** 组件样式 */
    private String style;
    
    /** X轴位置 */
    private BigDecimal x;
    
    /** Y轴位置 */
    private BigDecimal y;

    /** 组件数据/元数据 */
    private String meta;

    /** 逻辑删除：0-正常, 1-已删除 */
    private Integer deleted;
    
    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
