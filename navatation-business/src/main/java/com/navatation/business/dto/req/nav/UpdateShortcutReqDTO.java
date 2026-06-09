package com.navatation.business.dto.req.nav;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.nav.UpdateShortcutReqDTO;

@Data
public class UpdateShortcutReqDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;
}
