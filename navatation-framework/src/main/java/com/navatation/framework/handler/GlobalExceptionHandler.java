package com.navatation.framework.handler;

import com.navatation.common.BizException;
import com.navatation.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

/** @Author admin
 * @CreateTime 2026-05-15
 * @Description 全局异常处理器，统一处理业务异常、参数校验异常及未知异常 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleBizException(BizException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验失败异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        return Result.fail(400, msg);
    }

    /**
     * 处理 HTTP 方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return Result.fail(HttpStatus.METHOD_NOT_ALLOWED.value(), "不支持的请求方法");
    }

    /**
     * 处理 404 找不到路由异常
     * （注意：需要配置 spring.mvc.throw-exception-if-no-handler-found=true 才会进入这里）
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNoHandlerFound(NoHandlerFoundException e) {
        return Result.fail(HttpStatus.NOT_FOUND.value(), "请求接口不存在");
    }

    /**
     * 处理静态资源不存在异常 (Spring Boot 3)
     */
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNoResourceFound(org.springframework.web.servlet.resource.NoResourceFoundException e) {
        log.debug("静态资源不存在: {}", e.getMessage());
        return Result.fail(HttpStatus.NOT_FOUND.value(), "资源不存在: " + e.getResourcePath());
    }

    /**
     * 处理未知系统异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        log.error("Unexpected error", e);
        return Result.fail(500, "服务器内部错误");
    }
}
