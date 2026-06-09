package com.navatation.business.dto.resp;

import lombok.Data;

@Data
public class ToggleRespDTO {
    private String todoId;
    private Boolean completed;
    private String completedAt;
}
