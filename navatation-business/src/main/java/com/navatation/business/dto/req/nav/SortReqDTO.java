package com.navatation.business.dto.req.nav;

import lombok.Data;
import java.util.List;
import com.navatation.business.dto.req.nav.SortItemDTO;
import com.navatation.business.dto.req.nav.SortReqDTO;

/**
 * 排序请求参数
 *
 * @date 2026-06-09
 */
@Data
public class SortReqDTO {
    private List<SortItemDTO> items;
}
