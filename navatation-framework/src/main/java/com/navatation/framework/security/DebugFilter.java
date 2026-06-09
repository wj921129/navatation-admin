package com.navatation.framework.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DebugFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        System.out.println("=======> DEBUG FILTER: BEFORE URI=" + req.getRequestURI() + " ContextPath=" + req.getContextPath() + " ServletPath=" + req.getServletPath());
        chain.doFilter(request, response);
        System.out.println("=======> DEBUG FILTER: AFTER STATUS=" + res.getStatus() + " URI=" + req.getRequestURI());
    }
}
