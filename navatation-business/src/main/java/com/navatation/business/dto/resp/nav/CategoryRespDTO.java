package com.navatation.business.dto.resp.nav;

import lombok.Data;
import com.navatation.business.dto.resp.nav.CategoryRespDTO;

/**
 * CategoryRespDTO 功能描述
 *
 * @date 2026-06-09
 */
@Data
public class CategoryRespDTO {
    private String categoryId;
    private String name;
    private Double sortOrder;
    private Integer shortcutCount;
}
