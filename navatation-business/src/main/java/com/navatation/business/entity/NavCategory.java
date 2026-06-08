package com.navatation.business.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @Author admin
 * @CreateTime 2026-05-15
 * @Description 导航分类实体
 */
@Data
@TableName("navatation_nav_category")
public class NavCategory {
    private Long rowId;

    @TableId(type = IdType.INPUT)
    private String categoryId;
    
    private String userId;
    private String name;
    private Double sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
