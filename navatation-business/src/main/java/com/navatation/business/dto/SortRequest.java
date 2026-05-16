package com.navatation.business.dto;

import lombok.Data;
import java.util.List;

@Data
public class SortRequest {
    private List<SortItem> items;
}
