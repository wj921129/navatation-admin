package com.navatation.business.dto.req.nav;

import lombok.Data;
import com.navatation.business.dto.req.nav.SortItemDTO;

/**
 * SortItemDTO 功能描述
 *
 * @date 2026-06-09
 */
@Data
public class SortItemDTO {
    private String shortcutId;
    private Double sortOrder;
}
