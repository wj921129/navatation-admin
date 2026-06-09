package com.navatation.business.dto.req.todo;

import lombok.Data;
import java.util.List;
import com.navatation.business.dto.req.todo.TodoSortReqDTO;
import com.navatation.business.dto.req.todo.TodoSortItemDTO;

/**
 * TodoSortReqDTO 功能描述
 *
 * @date 2026-06-09
 */
@Data
public class TodoSortReqDTO {
    private List<TodoSortItemDTO> items;
}
