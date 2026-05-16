package com.navatation.business.dto;

import lombok.Data;
import java.util.List;

@Data
public class TodoSortRequest {
    private List<TodoSortItem> items;
}
