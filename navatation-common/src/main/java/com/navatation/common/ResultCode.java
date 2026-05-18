package com.navatation.common;

import lombok.Getter;

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description 统一错误码枚举 */
@Getter
public enum ResultCode {
    SUCCESS(200, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证或 Token 已过期"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "数据冲突"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // Business codes
    USERNAME_EXISTS(40901, "用户名已存在"),
    USER_NOT_FOUND(40401, "用户不存在"),
    PASSWORD_ERROR(40101, "用户名或密码错误"),
    TOKEN_EXPIRED(40102, "Token 已过期"),
    TOKEN_INVALID(40103, "Token 无效"),
    EMAIL_NOT_MATCH(40001, "用户名与绑定邮箱不匹配"),
    EMAIL_EMPTY(40002, "用户未绑定邮箱，无法找回密码"),
    NONCE_INVALID(40003, "请求已过期，请重新操作"),
    ENCRYPTION_ERROR(40004, "数据解密失败");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
