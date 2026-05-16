package com.navatation.business.dto;

import lombok.Data;
import java.util.List;

@Data
public class BatchCreateRequest {
    private Long categoryId;
    private List<CreateShortcutItem> shortcuts;
}
