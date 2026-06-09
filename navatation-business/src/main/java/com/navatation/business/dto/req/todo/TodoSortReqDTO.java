package com.navatation.business.dto.req.todo;

import lombok.Data;
import java.util.List;
import com.navatation.business.dto.req.todo.TodoSortReqDTO;
import com.navatation.business.dto.req.todo.TodoSortItemDTO;

/**
 * 待办事项排序请求参数
 *
 * @date 2026-06-09
 */
@Data
public class TodoSortReqDTO {
    private List<TodoSortItemDTO> items;
}
