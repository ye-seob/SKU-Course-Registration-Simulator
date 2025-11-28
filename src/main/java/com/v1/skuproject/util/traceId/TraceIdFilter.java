package com.v1.skuproject.util.traceId;

import jakarta.servlet.*;

import java.io.IOException;
import java.util.UUID;

/**
 * 요청과 응답 사이에서 Trace ID 생성 및 설정
 */
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String traceId = UUID.randomUUID().toString(); // Trace ID 생성

            TraceIdUtil.setTraceId(traceId);

            chain.doFilter(request, response);
        } finally {
            TraceIdUtil.clear();
        }
    }
}
