package com.navatation.business.dto.req.nav;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.nav.FaviconReqDTO;

@Data
public class FaviconReqDTO {
    @NotBlank
    private String url;
}
