package com.navatation.framework.aop;

import com.navatation.framework.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * @Author admin
 * @CreateTime 2026-05-20
 * @Description 全局日志及请求链路追踪切面，优先级最高
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLogAspect {

    private static final Logger log = LoggerFactory.getLogger(RequestLogAspect.class);
    private static final String TRACE_ID = "traceId";

    private final JwtTokenProvider jwtTokenProvider;

    public RequestLogAspect(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Pointcut("execution(* com.navatation.business.controller..*.*(..))")
        /**
     * controllerPointcut 方法
     */
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
        /**
     * around 方法
     */
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 生成 32 位随机 UUID (去除横线) 并在其前追加 "UUID="，放入 MDC 日志上下文
        String traceId = "UUID=" + UUID.randomUUID().toString().replace("-", "");
        MDC.put(TRACE_ID, traceId);

        long startTime = System.currentTimeMillis();
        HttpServletRequest request = getRequest();

        String ip = "Unknown";
        String url = "Unknown";
        String userIdStr = "Anonymous";

        if (request != null) {
            // 2. 提取客户端 IP（支持多级反向代理）
            ip = getClientIp(request);
            // 3. 提取请求 URL
            url = request.getRequestURI();
            // 4. 提取用户 userId
            userIdStr = getUserId(request);
        }

        log.info("[Request Start] IP: {}, URL: {}, UserID: {}, Class: {}.{}", 
                ip, url, userIdStr, 
                joinPoint.getTarget().getClass().getSimpleName(), 
                joinPoint.getSignature().getName());

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            log.info("[Request End] Success - IP: {}, URL: {}, UserID: {}, Duration: {}ms", ip, url, userIdStr, duration);
            return result;
        } catch (Throwable e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("[Request End] Failed - IP: {}, URL: {}, UserID: {}, Duration: {}ms, Error: {}", ip, url, userIdStr, duration, e.getMessage());
            throw e;
        } finally {
            // 5. 请求结束，务必清理 MDC，防止 Web 容器线程池中的 ThreadLocal 内存泄漏
            MDC.remove(TRACE_ID);
        }
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多级反向代理，提取第一个真实 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    private String getUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String userId = jwtTokenProvider.getUserIdFromAuthHeader(authHeader);
                return userId != null ? userId : "Anonymous";
            } catch (Exception e) {
                log.warn("解析 Token 提取 UserID 失败, 降级为匿名访问", e);
                // Token 无效或过期时直接降级为匿名访问，不破坏正常业务响应
                return "Anonymous";
            }
        }
        return "Anonymous";
    }
}
