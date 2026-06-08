package com.navatation.business.entity.root;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author root
 * @CreateTime 2026-06-08
 * @Description 管理员组件实体
 */
@Data
@TableName("navatation_root_user_widget")
public class RootWidget {
    private Long rowId;

    @TableId(type = IdType.INPUT)
    private String widgetId;
    
    private String userId;
    private String type;
    private String style;
    private BigDecimal x;
    private BigDecimal y;
    private String meta;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
