package com.navatation.business.dto;

import lombok.Data;

@Data
public class UserVO {
    private Long userId;
    private String username;
    private String avatar;
    private String createdAt;
}
