package com.navatation.business.dto.resp;

import lombok.Data;

@Data
public class TodoRespDTO {
    private String todoId;
    private String content;
    private Boolean completed;
    private Double sortOrder;
    private String createdAt;
    private String completedAt;
}
