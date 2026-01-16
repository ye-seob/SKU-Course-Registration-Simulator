package com.v1.skuproject.common.exception;

import com.v1.skuproject.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice //  @Controller나 @RestController에서 발생하는 예외를 가로챔
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Object>> handleBaseException(BaseException ex) {
        // 직접 정의한 BaseException를 처리

        ErrorCode errorCode = ex.getErrorCode();

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .data(null)
                .code(errorCode.getCode())    // 오류 코드 (ex: U_404)
                .message(errorCode.getMessage())
                .build();

        log.debug(
                "예외 발생 code={}, message={}",
                errorCode.getCode(),
                errorCode.getMessage()
        );


        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }




    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {

        // 첫 번째 필드 에러 메시지만 가져오기 (원하면 모든 필드도 가능)
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("요청 값이 올바르지 않습니다.");

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .data(null)
                .code("VALIDATION_ERROR")
                .message(message)
                .build();

        log.debug("Validation 예외 발생 message={}", message);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {
        //  BaseException으로 처리되지 않은 예외를 처리
        ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(false)
                .data(null)
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();

        log.error(
                "예상치 못한 서버 오류 발생 type={}, message={}",
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }
}
