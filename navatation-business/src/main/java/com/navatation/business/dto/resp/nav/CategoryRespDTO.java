package com.navatation.business.dto.resp.nav;

import lombok.Data;
import com.navatation.business.dto.resp.nav.CategoryRespDTO;

/**
 * 导航分类信息响应结果
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
