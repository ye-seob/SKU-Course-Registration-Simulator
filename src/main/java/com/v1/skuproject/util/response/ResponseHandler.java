package com.v1.skuproject.util.response;

import com.v1.skuproject.util.traceId.TraceIdUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {
    // 모든 컨트롤러에서 응답을 표준화하는 데 사용

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {

        String traceId = TraceIdUtil.getTraceId();

        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .code("SUCCESS")
                .message("요청이 정상 처리되었습니다.")
                .traceId(traceId)
                .build();

        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        // POST 요청 성공 시 호출하는 메서드

        String traceId = TraceIdUtil.getTraceId();

        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .code("CREATED")
                .message("리소스가 생성되었습니다.")
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String code, String message, HttpStatus status) {
        // 오류 발생 시 오류 처리 메서드

        String traceId = TraceIdUtil.getTraceId();

        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(false)
                .data(null)
                .code(code)
                .message(message)
                .traceId(traceId)
                .build();

        return new ResponseEntity<>(response, status);
    }
}
