package com.navatation.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FaviconRequest {
    @NotBlank
    private String url;
}
