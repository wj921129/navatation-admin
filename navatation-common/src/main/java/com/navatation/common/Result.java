package com.navatation.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description 统一响应封装类 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String message;
    private T data;
    private long timestamp;

    /**
     * 成功响应（无自定义消息）
     * @param <T> 数据类型
     * @param data 响应数据
     * @return 成功结果 */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data, System.currentTimeMillis());
    }

    /**
     * 成功响应（含自定义消息）
     * @param <T> 数据类型
     * @param message 自定义消息
     * @param data 响应数据
     * @return 成功结果 */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data, System.currentTimeMillis());
    }

    /**
     * 失败响应（自定义错误码和消息）
     * @param <T> 数据类型
     * @param code 错误码
     * @param message 错误消息
     * @return 失败结果 */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null, System.currentTimeMillis());
    }

    /**
     * 失败响应（使用枚举错误码）
     * @param <T> 数据类型
     * @param resultCode 错误码枚举
     * @return 失败结果 */
    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null, System.currentTimeMillis());
    }
}
