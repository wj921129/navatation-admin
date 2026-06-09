package com.navatation.business.dto.req.nav;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.navatation.business.dto.req.nav.FaviconReqDTO;

/**
 * 获取网站图标(Favicon)请求参数
 *
 * @date 2026-06-09
 */
@Data
public class FaviconReqDTO {
    @NotBlank
    private String url;
}
