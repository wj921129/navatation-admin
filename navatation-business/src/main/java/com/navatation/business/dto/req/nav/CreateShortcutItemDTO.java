package com.navatation.business.dto.req.nav;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.nav.CreateShortcutItemDTO;

/**
 * CreateShortcutItemDTO 功能描述
 *
 * @date 2026-06-09
 */
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
