package com.navatation.business.dto;

import lombok.Data;

@Data
public class ToggleVO {
    private String todoId;
    private Boolean completed;
    private String completedAt;
}
