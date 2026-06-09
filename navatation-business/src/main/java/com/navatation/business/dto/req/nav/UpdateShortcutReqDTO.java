package com.navatation.business.dto.req.nav;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.nav.UpdateShortcutReqDTO;

/**
 * UpdateShortcutReqDTO 功能描述
 *
 * @date 2026-06-09
 */
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
