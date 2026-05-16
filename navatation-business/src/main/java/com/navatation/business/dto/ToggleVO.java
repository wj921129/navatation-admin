package com.navatation.business.dto;

import lombok.Data;

@Data
public class ToggleVO {
    private Long todoId;
    private Boolean completed;
    private String completedAt;
}
