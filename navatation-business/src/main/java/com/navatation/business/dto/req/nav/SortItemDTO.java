package com.navatation.business.dto.req.nav;

import lombok.Data;
import com.navatation.business.dto.req.nav.SortItemDTO;

@Data
public class SortItemDTO {
    private String shortcutId;
    private Double sortOrder;
}
