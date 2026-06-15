package com.navatation.business.dto.resp.nav;

import lombok.Data;
import java.math.BigDecimal;
import com.navatation.business.dto.resp.nav.ShortcutRespDTO;

/**
 * 快捷方式信息响应结果
 *
 * @date 2026-06-09
 */
@Data
public class ShortcutRespDTO {
    private String shortcutId;
    private String categoryId;
    private String name;
    private String url;
    private String iconType;
    private String iconValue;
    private String iconColor;
    private BigDecimal sortOrder;
    private String createdAt;
}
