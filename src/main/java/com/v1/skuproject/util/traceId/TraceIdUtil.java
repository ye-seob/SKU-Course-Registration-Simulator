package com.v1.skuproject.util.traceId;

import org.slf4j.MDC;

public class TraceIdUtil {

    private static final String TRACE_ID_KEY = "traceId";
    // Trace ID를 저장할 때 사용할 키 정의

    public static String getTraceId() {
        // Trace ID 조회 메서드
        return MDC.get(TRACE_ID_KEY);
    }

    public static void setTraceId(String traceId) {
        // 현재 스레드에 Trace ID를 저장
        MDC.put(TRACE_ID_KEY, traceId);
    }

    public static void clear() {
        MDC.clear();
    }
}
