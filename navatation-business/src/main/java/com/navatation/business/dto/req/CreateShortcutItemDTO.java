package com.navatation.business.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateShortcutItemDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String url;
    private String iconType = "BUILTIN";
    private String iconValue;
    private String iconColor;
}
