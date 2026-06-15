package com.navatation.business.entity.nav;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @Description 普通用户首页快捷方式实体
 */
@Data
@TableName("navatation_nav_home_shortcut")
public class NavHomeShortcut {
    private Long rowId;

    @TableId(type = IdType.INPUT)
    private String shortcutId;
    
    private String userId;
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;
    private Double sortOrder;
    private Long clickCount;
    private LocalDateTime lastClickAt;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
