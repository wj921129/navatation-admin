package com.navatation.business.entity.recommend;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @Description 推荐全局配置实体，供管理员操作与游客默认使用
 */
@Data
@TableName("navatation_recommend_config")
public class RecommendConfig {
    private Long rowId;

    @TableId(type = IdType.INPUT)
    private String configId;
    
    private String searchEngine;
    private String backgroundImage;
    private String backgroundType;
    private Integer searchBoxWidth;
    private Integer searchBoxHeight;
    private Integer searchBoxMarginTop;
    private Integer iconSize;
    private Integer iconRadius;
    private Integer iconSpacingX;
    private Integer iconSpacingY;
    private Integer iconTextGap;
    private Integer textSize;
    private Integer iconsMarginTop;
    private Integer iconsMarginX;
    private String theme;

    /** 逻辑删除：0-正常, 1-已删除 */
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
