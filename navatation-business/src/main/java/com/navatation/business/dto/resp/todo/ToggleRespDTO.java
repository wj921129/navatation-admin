package com.navatation.business.dto.resp.todo;

import lombok.Data;
import com.navatation.business.dto.resp.todo.ToggleRespDTO;

@Data
public class ToggleRespDTO {
    private String todoId;
    private Boolean completed;
    private String completedAt;
}
