package com.navatation.business.entity.recommend;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Description 推荐待办事项实体
 */
@Data
@TableName("navatation_recommend_todo_item")
public class RecommendTodoItem {
    private Long rowId;

    @TableId(type = IdType.INPUT)
    private String todoId;
    
    private String content;
    private BigDecimal sortOrder;

    /** 逻辑删除：0-正常, 1-已删除 */
    private Integer deleted;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
