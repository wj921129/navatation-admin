package com.navatation.business.dto.resp.user;

import lombok.Data;
import com.navatation.business.dto.resp.user.UserRespDTO;

/**
 * 用户基础信息响应结果
 *
 * @date 2026-06-09
 */
@Data
public class UserRespDTO {
    private String userId;
    private String username;
    private String avatar;
    private String role;
    private String createdAt;
}
