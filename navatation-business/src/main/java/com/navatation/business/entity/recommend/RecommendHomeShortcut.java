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
 * @Description 推荐首页快捷方式实体
 */
@Data
@TableName("navatation_recommend_home_shortcut")
public class RecommendHomeShortcut {
    private Long rowId;

    @TableId(type = IdType.INPUT)
    private String shortcutId;
    
    private String type;
    private String stackId;
    private String stackName;
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;
    private BigDecimal sortOrder;

    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
