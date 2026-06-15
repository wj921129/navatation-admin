package com.navatation.business.dto.req.todo;

import lombok.Data;
import java.math.BigDecimal;
import com.navatation.business.dto.req.todo.TodoSortItemDTO;

/**
 * 待办事项排序子项参数
 *
 * @date 2026-06-09
 */
@Data
public class TodoSortItemDTO {
    private String todoId;
    private BigDecimal sortOrder;
}
