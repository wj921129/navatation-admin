package com.navatation.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateShortcutRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;
}
