package com.navatation.business.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FaviconReqDTO {
    @NotBlank
    private String url;
}
