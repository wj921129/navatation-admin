package com.navatation.business.entity.user;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @Author admin
 * @CreateTime 2026-05-15
 * @Description 用户实体
 */
@Data
@TableName("navatation_user")
public class User {
    private Long rowId;
    
    @TableId(type = IdType.INPUT)
    private String userId;
    
    private String username;
    private String password;
    private String email;
    private String avatar;
    private String role;
    private Integer status;
    private LocalDateTime lastLoginAt;
    private String lastLoginIp;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
