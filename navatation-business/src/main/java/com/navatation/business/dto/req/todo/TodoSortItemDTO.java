package com.navatation.business.dto.req.todo;

import lombok.Data;
import com.navatation.business.dto.req.todo.TodoSortItemDTO;

@Data
public class TodoSortItemDTO {
    private String todoId;
    private Double sortOrder;
}
