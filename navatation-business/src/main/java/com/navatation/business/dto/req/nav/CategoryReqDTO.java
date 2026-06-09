package com.navatation.business.dto.req.nav;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.nav.CategoryReqDTO;

/**
 * 导航分类操作请求参数
 *
 * @date 2026-06-09
 */
@Data
public class CategoryReqDTO {
    @NotBlank
    private String name;
    private Double sortOrder;
}
