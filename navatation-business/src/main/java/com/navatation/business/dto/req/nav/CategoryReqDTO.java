package com.navatation.business.dto.req.nav;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.nav.CategoryReqDTO;

/**
 * CategoryReqDTO 功能描述
 *
 * @date 2026-06-09
 */
@Data
public class CategoryReqDTO {
    @NotBlank
    private String name;
    private Double sortOrder;
}
