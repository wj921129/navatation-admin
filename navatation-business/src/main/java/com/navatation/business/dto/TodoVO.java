package com.navatation.business.dto;

import lombok.Data;

@Data
public class TodoVO {
    private Long todoId;
    private String content;
    private Boolean completed;
    private Integer sortOrder;
    private String createdAt;
    private String completedAt;
}
