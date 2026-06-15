package com.navatation.business.entity.user;

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
 * @Description 用户配置实体，存储搜索、布局、主题等个性化设置
 */
@Data
@TableName("navatation_user_config")
public class UserConfig {
    private Long rowId;

    @TableId(type = IdType.INPUT)
    private String configId;
    
    private String userId;
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
