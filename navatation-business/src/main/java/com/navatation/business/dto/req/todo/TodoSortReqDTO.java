package com.navatation.business.dto.req.todo;

import lombok.Data;
import java.util.List;
import com.navatation.business.dto.req.todo.TodoSortReqDTO;
import com.navatation.business.dto.req.todo.TodoSortItemDTO;

@Data
public class TodoSortReqDTO {
    private List<TodoSortItemDTO> items;
}
