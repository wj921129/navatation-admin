package com.navatation.business.dto.req.todo;

import lombok.Data;
import com.navatation.business.dto.req.todo.TodoSortItemDTO;

/**
 * TodoSortItemDTO 功能描述
 *
 * @date 2026-06-09
 */
@Data
public class TodoSortItemDTO {
    private String todoId;
    private Double sortOrder;
}
