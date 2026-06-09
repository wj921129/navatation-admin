package com.navatation.business.dto.resp.nav;

import lombok.Data;
import com.navatation.business.dto.resp.nav.BatchCreateItemRespDTO;

/**
 * 批量创建子项结果响应
 *
 * @date 2026-06-09
 */
@Data
public class BatchCreateItemRespDTO {
    private String shortcutId;
    private String name;
}
