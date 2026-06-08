package com.navatation.business.dto;

import lombok.Data;

@Data
public class TodoVO {
    private String todoId;
    private String content;
    private Boolean completed;
    private Double sortOrder;
    private String createdAt;
    private String completedAt;
}
