package com.navatation.business.dto.resp.nav;

import lombok.Data;
import java.util.List;

/**
 * 获取网站图标(Favicon)响应结果
 *
 * @date 2026-06-09
 */
@Data
public class FaviconRespDTO {
    private List<String> faviconUrls;
    private String sourceUrl;
}
