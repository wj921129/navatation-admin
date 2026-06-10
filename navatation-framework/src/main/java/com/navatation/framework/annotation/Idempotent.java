package com.navatation.framework.annotation;

import java.lang.annotation.*;

/**
 * @Author PM
 * @Description 接口防抖与幂等性注解
 * 用于限制同一用户对同一接口的频繁提交
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 防抖锁定时间（单位：秒）
     * 默认 5 秒
     */
    long timeout() default 5;

    /**
     * 触发防抖时的提示信息
     */
    String message() default "请勿频繁重复提交";
}
