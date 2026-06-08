package com.navatation.business;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成与加密辅助测试类
 * 
 * - 用于为用户（如默认管理员 admin）生成符合系统安全规范的 BCrypt 加密 HASH 密码。
 * - 生成的 HASH 密码可直接用于更新数据库表的 `password` 字段（例如 ddl.sql 初始化脚本中）。
 */
public class PasswordGenTest {

    @Test
    public void generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);
        
        System.out.println("==================================================");
        System.out.println("原始明文密码: " + rawPassword);
        System.out.println("====== HASH_START (请复制下方密文到数据库) ======");
        System.out.println(encodedPassword);
        System.out.println("====== HASH_END ================================");
        System.out.println("==================================================");
    }
}
