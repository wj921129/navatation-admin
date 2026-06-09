package com.navatation.business.dto.resp.todo;

import lombok.Data;
import com.navatation.business.dto.resp.todo.ToggleRespDTO;

/**
 * 状态切换操作响应结果
 *
 * @date 2026-06-09
 */
@Data
public class ToggleRespDTO {
    private String todoId;
    private Boolean completed;
    private String completedAt;
}
