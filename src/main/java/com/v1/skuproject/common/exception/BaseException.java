package com.v1.skuproject.common.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    // RuntimeException을 상속함으로써 컨트롤러나 서비스 계층에서 try-catch로 감싸지 않아도 됨

    private final ErrorCode errorCode; //    ErrorCode : HTTP 상태 코드, 오류 코드, 메시지

    public BaseException(ErrorCode errorCode) {

        super(errorCode.getMessage());

        this.errorCode = errorCode;
    }
}
