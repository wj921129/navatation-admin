package com.navatation.business.dto.req;

import lombok.Data;
import java.util.List;

@Data
public class BatchCreateReqDTO {
    private String categoryId;
    private List<CreateShortcutItem> shortcuts;
}
