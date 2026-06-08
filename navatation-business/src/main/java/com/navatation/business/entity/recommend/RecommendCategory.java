package com.navatation.business.entity.recommend;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @Author admin
 * @CreateTime 2026-06-08
 * @Description 推荐网址分类实体
 */
@Data
@TableName("navatation_recommend_category")
public class RecommendCategory {
    private Long rowId;
    
    @TableId(type = IdType.INPUT)
    private String categoryId;
    
    private String name;
    @TableField("icon_name")
    private String icon;
    private Double sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
