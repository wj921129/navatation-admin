package com.navatation.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TodoUpdateRequest {
    @NotBlank
    private String content;
}
