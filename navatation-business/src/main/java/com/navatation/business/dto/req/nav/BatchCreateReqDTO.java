package com.navatation.business.dto.req.nav;

import lombok.Data;
import java.util.List;
import com.navatation.business.dto.req.nav.BatchCreateReqDTO;
import com.navatation.business.dto.req.nav.CreateShortcutItemDTO;

@Data
public class BatchCreateReqDTO {
    private String categoryId;
    private List<CreateShortcutItemDTO> shortcuts;
}
