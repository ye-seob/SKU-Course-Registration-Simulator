package com.v1.skuproject.util.traceId;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 요청과 응답 사이에서 Trace ID 생성 및 설정
 */
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        try {
            String traceId = UUID.randomUUID().toString(); // Trace ID 생성

            TraceIdUtil.setTraceId(traceId);

            response.setHeader("X-Trace-Id", traceId);

            chain.doFilter(request, response);
        } finally {
            TraceIdUtil.clear();
        }
    }
}
