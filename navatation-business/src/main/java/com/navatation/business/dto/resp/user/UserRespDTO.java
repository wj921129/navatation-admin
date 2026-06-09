package com.navatation.business.dto.resp.user;

import lombok.Data;
import com.navatation.business.dto.resp.user.UserRespDTO;

@Data
public class UserRespDTO {
    private String userId;
    private String username;
    private String avatar;
    private String role;
    private String createdAt;
}
