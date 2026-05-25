package com.navatation.business.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TodoCreateRequest {
    @NotBlank(message = "待办内容不能为空")
    @Size(max = 512, message = "待办内容不能超过512个字符")
    private String content;
}
