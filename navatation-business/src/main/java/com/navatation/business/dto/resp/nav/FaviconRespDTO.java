package com.navatation.business.dto.resp.nav;

import lombok.Data;
import com.navatation.business.dto.resp.nav.FaviconRespDTO;

/**
 * 获取网站图标(Favicon)响应结果
 *
 * @date 2026-06-09
 */
@Data
public class FaviconRespDTO {
    private String faviconUrl;
    private String sourceUrl;
}
