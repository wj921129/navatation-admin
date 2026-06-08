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
 * @Description 管理员待办事项实体
 */
@Data
@TableName("navatation_root_todo_item")
public class RootTodoItem {
    private Long rowId;

    @TableId(type = IdType.INPUT)
    private String todoId;
    
    private String userId;
    private String content;
    private Boolean completed;
    private Double sortOrder;
    private LocalDateTime completedAt;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
