package com.navatation.business.dto;

import lombok.Data;
import java.util.List;

@Data
public class BatchCreateRequest {
    private String categoryId;
    private List<CreateShortcutItem> shortcuts;
}
