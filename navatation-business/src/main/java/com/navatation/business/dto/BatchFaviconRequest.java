package com.navatation.business.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

/**
 * 批量获取 Favicon 的请求对象
 */
@Data
public class BatchFaviconRequest {
    @NotEmpty(message = "URL列表不能为空")
    private List<String> urls;
}
