package com.navatation.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TodoCreateRequest {
    @NotBlank
    private String content;
}
