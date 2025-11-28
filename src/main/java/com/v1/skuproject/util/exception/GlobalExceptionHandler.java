package com.v1.skuproject.util.exception;

import com.v1.skuproject.util.response.ApiResponse;
import com.v1.skuproject.util.traceId.TraceIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice //  @Controller나 @RestController에서 발생하는 예외를 가로챔
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Object>> handleBaseException(BaseException ex) {
        // 직접 정의한 BaseException를 처리

        ErrorCode errorCode = ex.getErrorCode();

        String traceId = TraceIdUtil.getTraceId();

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .data(null)
                .code(errorCode.getCode())    // 오류 코드 (ex: U_404)
                .message(errorCode.getMessage())
                .traceId(traceId)
                .build();

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        //  BaseException으로 처리되지 않은 예외를 처리
        String traceId = TraceIdUtil.getTraceId();
        log.error("traceId={} | 예외={} | message={}", traceId, ex.getClass().getSimpleName(), ex.getMessage());

        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .data(null)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .traceId(traceId)
                .build();

        log.error(response.toString());
        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
}
