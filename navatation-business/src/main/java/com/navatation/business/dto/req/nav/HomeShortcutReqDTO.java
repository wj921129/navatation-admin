package com.navatation.business.dto.req.nav;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HomeShortcutReqDTO {
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;
    private BigDecimal sortOrder;
}
