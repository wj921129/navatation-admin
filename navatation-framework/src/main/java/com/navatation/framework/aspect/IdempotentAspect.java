package com.navatation.framework.aspect;

import com.navatation.common.BizException;
import com.navatation.common.RedisConstants;
import com.navatation.framework.annotation.Idempotent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @Author PM
 * @Description 接口防抖与幂等性切面实现
 * 拦截 @Idempotent 注解，利用 Redis setIfAbsent 保证请求唯一性
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class IdempotentAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        HttpServletRequest request = attributes.getRequest();

        String userId = "anonymous";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String) {
            userId = (String) auth.getPrincipal();
        }

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String argsString = Arrays.toString(joinPoint.getArgs());

        // 计算参数特征 MD5
        String hash = DigestUtils.md5DigestAsHex(argsString.getBytes(StandardCharsets.UTF_8));
        String key = RedisConstants.KEY_SYS_IDEMPOTENT + userId + ":" + method + ":" + uri + ":" + hash;

        // Redis 分布式锁防抖
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "1", idempotent.timeout(), TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(success)) {
            log.warn("触发接口防抖保护 - User: {}, URI: {}", userId, uri);
            throw new BizException(429, idempotent.message());
        }

        return joinPoint.proceed();
    }
}
