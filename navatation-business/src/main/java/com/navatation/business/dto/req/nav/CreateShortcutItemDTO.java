package com.navatation.business.dto.req.nav;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.nav.CreateShortcutItemDTO;

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
