package com.navatation.business;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description Spring Boot 启动类 */
@SpringBootApplication(scanBasePackages = "com.navatation")
@MapperScan("com.navatation.business.mapper")
public class NavatationApplication {
        /**
     * main 方法
     */
    public static void main(String[] args) {
        SpringApplication.run(NavatationApplication.class, args);
    }
}
