package com.navatation.business.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateShortcutItem {
    @NotBlank
    private String name;
    @NotBlank
    private String url;
    private String iconType = "BUILTIN";
    private String iconValue;
    private String iconColor;
}
