package com.navatation.business.dto.resp.nav;

import lombok.Data;
import com.navatation.business.dto.resp.nav.CategoryRespDTO;

@Data
public class CategoryRespDTO {
    private String categoryId;
    private String name;
    private Double sortOrder;
    private Integer shortcutCount;
}
