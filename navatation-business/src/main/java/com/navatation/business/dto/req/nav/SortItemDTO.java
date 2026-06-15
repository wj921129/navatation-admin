package com.navatation.business.dto.req.nav;

import lombok.Data;
import java.math.BigDecimal;
import com.navatation.business.dto.req.nav.SortItemDTO;

/**
 * 排序子项参数
 *
 * @date 2026-06-09
 */
@Data
public class SortItemDTO {
    private String shortcutId;
    private BigDecimal sortOrder;
}
