package com.navatation.business.dto.req.nav;

import lombok.Data;
import java.util.List;
import com.navatation.business.dto.req.nav.SortItemDTO;
import com.navatation.business.dto.req.nav.SortReqDTO;

@Data
public class SortReqDTO {
    private List<SortItemDTO> items;
}
