package com.navatation.business.entity.root;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @Author root
 * @CreateTime 2026-06-08
 * @Description 管理员快捷方式实体
 */
@Data
@TableName("navatation_root_nav_shortcut")
public class RootShortcut {
    private Long rowId;

    @TableId(type = IdType.INPUT)
    private String shortcutId;
    
    private String categoryId;
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
