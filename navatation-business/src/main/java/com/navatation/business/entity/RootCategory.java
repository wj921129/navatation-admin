package com.navatation.business.entity;

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
 * @Description 管理员导航分类实体
 */
@Data
@TableName("navatation_root_nav_category")
public class RootCategory {
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
