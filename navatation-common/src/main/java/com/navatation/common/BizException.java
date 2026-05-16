package com.navatation.common;

import lombok.Getter;

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description 业务异常类，携带错误码和错误消息 */
@Getter
public class BizException extends RuntimeException {
    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }
}
