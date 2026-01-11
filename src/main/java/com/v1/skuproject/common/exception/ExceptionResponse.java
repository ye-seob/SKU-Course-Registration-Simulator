package com.v1.skuproject.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
    //  예외 발생 시 클라이언트에게 반환될 오류 정보
    private final String code;// 오류의 상세 코드

    private final String message;
}
