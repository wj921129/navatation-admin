package com.navatation.business.dto;

import lombok.Data;

@Data
public class UserVO {
    private String userId;
    private String username;
    private String avatar;
    private String role;
    private String createdAt;
}
