package com.navatation.business.dto.resp.user;

import lombok.Data;
import com.navatation.business.dto.resp.user.UserRespDTO;

/**
 * UserRespDTO 功能描述
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
