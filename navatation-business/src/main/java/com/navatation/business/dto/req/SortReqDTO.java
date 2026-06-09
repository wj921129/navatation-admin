package com.navatation.business.dto.req;

import lombok.Data;
import java.util.List;

@Data
public class SortReqDTO {
    private List<SortItemDTO> items;
}
