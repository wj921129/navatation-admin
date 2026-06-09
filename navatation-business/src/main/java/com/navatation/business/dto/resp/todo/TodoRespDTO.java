package com.navatation.business.dto.resp.todo;

import lombok.Data;
import com.navatation.business.dto.resp.todo.TodoRespDTO;

@Data
public class TodoRespDTO {
    private String todoId;
    private String content;
    private Boolean completed;
    private Double sortOrder;
    private String createdAt;
    private String completedAt;
}
