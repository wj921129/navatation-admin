package com.navatation.business.dto.resp.nav;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.navatation.business.dto.resp.nav.IconUploadRespDTO;

/**
 * 图标上传响应 VO
 */
@Data
@AllArgsConstructor
public class IconUploadRespDTO {
    private String iconUrl;
}
