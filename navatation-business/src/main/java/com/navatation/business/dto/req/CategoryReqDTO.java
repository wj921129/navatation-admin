package com.navatation.business.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryReqDTO {
    @NotBlank
    private String name;
    private Double sortOrder;
}
