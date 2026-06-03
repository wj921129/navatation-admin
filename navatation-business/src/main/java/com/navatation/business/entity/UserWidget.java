package com.navatation.business.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author admin
 * @CreateTime 2026-06-03
 * @Description 用户组件实体类，持久化存储用户在桌面上自定义添加的组件信息
 */
@Data
@TableName("navatation_user_widget")
public class UserWidget {

    /**
     * 自增物理主键
     */
    private Long rowId;

    /**
     * 组件唯一ID，业务逻辑主键
     */
    @TableId(type = IdType.INPUT)
    private String widgetId;

    /**
     * 所属用户ID
     */
    private String userId;

    /**
     * 组件类型
     */
    private String type;

    /**
     * 组件样式
     */
    private String style;

    /**
     * X轴百分比位置 (0.00 - 100.00)
     */
    private BigDecimal x;

    /**
     * Y轴百分比位置 (0.00 - 100.00)
     */
    private BigDecimal y;

    /**
     * 可选元数据，JSON 格式字符串
     */
    private String meta;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
