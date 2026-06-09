package com.navatation.business.dto.req.recommend;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import com.navatation.business.dto.req.recommend.RecommendSiteItemDTO;

/**
 * 推荐网址批量项 DTO
 * 
 * - 用于管理员批量编辑时单条网址数据的新增、更新与删除比对。
 * 
 * @author Antigravity
 * @date 2026-06-08
 */
@Data
public class RecommendSiteItemDTO {
    /** 推荐网站ID，可空。若为空则表示新增，否则为修改 */
    private String siteId;
    
    /** 网站名称 */
    @NotBlank(message = "网站名称不能为空")
    private String name;
    
    /** 网站URL */
    @NotBlank(message = "网站链接不能为空")
    private String url;
    
    /** 图标类型：BUILTIN / FAVICON / CUSTOM_URL / CUSTOM_UPLOAD */
    private String iconType;
    
    /** 图标值（内置名称、网络 Favicon 链接或上传文件路径） */
    private String iconValue;
    
    /** 图标展示颜色（仅内置图标类型生效） */
    private String iconColor;
    

}
