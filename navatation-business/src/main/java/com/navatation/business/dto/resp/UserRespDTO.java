package com.navatation.business.dto.resp;

import lombok.Data;

@Data
public class UserRespDTO {
    private String userId;
    private String username;
    private String avatar;
    private String role;
    private String createdAt;
}
