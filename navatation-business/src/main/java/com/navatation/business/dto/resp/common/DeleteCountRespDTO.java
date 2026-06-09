package com.navatation.business.dto.resp.common;

import lombok.Data;
import com.navatation.business.dto.resp.common.DeleteCountRespDTO;

/**
 * 批量删除统计结果响应
 *
 * @date 2026-06-09
 */
@Data
public class DeleteCountRespDTO {
    private Integer deletedCount;
}
