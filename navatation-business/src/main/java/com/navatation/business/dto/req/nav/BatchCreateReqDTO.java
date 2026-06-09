package com.navatation.business.dto.req.nav;

import lombok.Data;
import java.util.List;
import com.navatation.business.dto.req.nav.BatchCreateReqDTO;
import com.navatation.business.dto.req.nav.CreateShortcutItemDTO;

/**
 * 批量创建导航/快捷方式请求参数
 *
 * @date 2026-06-09
 */
@Data
public class BatchCreateReqDTO {
    private String categoryId;
    private List<CreateShortcutItemDTO> shortcuts;
}
