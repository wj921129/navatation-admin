package com.navatation.business.dto.req.nav;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.nav.FaviconReqDTO;

/**
 * FaviconReqDTO 功能描述
 *
 * @date 2026-06-09
 */
@Data
public class FaviconReqDTO {
    @NotBlank
    private String url;
}
