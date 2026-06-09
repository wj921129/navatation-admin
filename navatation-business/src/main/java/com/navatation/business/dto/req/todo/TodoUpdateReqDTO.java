package com.navatation.business.dto.req.todo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.navatation.business.dto.req.todo.TodoUpdateReqDTO;

/**
 * TodoUpdateReqDTO 功能描述
 *
 * @date 2026-06-09
 */
@Data
public class TodoUpdateReqDTO {
    @NotBlank(message = "待办内容不能为空")
    @Size(max = 512, message = "待办内容不能超过512个字符")
    private String content;
}
