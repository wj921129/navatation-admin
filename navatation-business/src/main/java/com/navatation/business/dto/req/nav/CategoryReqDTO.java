package com.navatation.business.dto.req.nav;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.nav.CategoryReqDTO;

@Data
public class CategoryReqDTO {
    @NotBlank
    private String name;
    private Double sortOrder;
}
