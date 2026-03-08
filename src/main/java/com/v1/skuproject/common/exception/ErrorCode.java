package com.v1.skuproject.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 사용자 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_ALREADY_EXISTS", "이미 존재하는 사용자입니다."),

    // 인증/인가 관련
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "접근 권한이 없습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "TOKEN_INVALID", "유효하지 않은 토큰입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH", "비밀번호가 일치하지 않습니다."),

    // 요청 관련
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
    PARAMETER_MISSING(HttpStatus.BAD_REQUEST, "PARAMETER_MISSING", "필수 파라미터가 누락되었습니다."),

    // 강의 관련
    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "LECTURE_NOT_FOUND", "존재하지 않는 강의입니다."),
    LECTURE_RATING_INVALID_SCORE(HttpStatus.BAD_REQUEST, "LECTURE_RATING_INVALID_SCORE", "평점은 1점에서 5점 사이여야 합니다."),
    LECTURE_RATING_NOT_ALLOWED(HttpStatus.FORBIDDEN, "LECTURE_RATING_NOT_ALLOWED", "강의 평점을 등록할 수 없습니다."),

    // 수강신청 관련
    ENROLLMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "ENROLLMENT_NOT_FOUND", "신청 내역이 존재하지 않습니다."),
    ENROLLMENT_INVALID_CANCEL(HttpStatus.BAD_REQUEST, "ENROLLMENT_INVALID_CANCEL", "수강 신청 취소를 할 수 없습니다"),
    ENROLLMENT_DUPLICATE(HttpStatus.BAD_REQUEST, "ENROLLMENT_DUPLICATE", "이미 신청한 강의입니다."),
    ENROLLMENT_CAPACITY_EXCEEDED(HttpStatus.BAD_REQUEST, "ENROLLMENT_CAPACITY_EXCEEDED", "강의 정원이 초과되었습니다."),
    ENROLLMENT_MAX_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "ENROLLMENT_MAX_LIMIT_EXCEEDED", "최대 수강 가능 개수를 초과했습니다."),
    ENROLLMENT_TIME_CONFLICT(HttpStatus.BAD_REQUEST, "ENROLLMENT_TIME_CONFLICT", "강의 시간이 겹칩니다."),
    ENROLLMENT_TIME_INVALID(HttpStatus.FORBIDDEN, "ENROLLMENT_TIME_INVALID", "수강신청 가능한 시간이 아닙니다."),

    //  장바구니 관련
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "CART_NOT_FOUND", "장바구니가 존재하지 않습니다."),
    CART_LECTURE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "CART_LECTURE_ALREADY_EXISTS", "이미 장바구니에 담긴 강의입니다."),
    CART_LECTURE_NOT_FOUND(HttpStatus.BAD_REQUEST, "CART_LECTURE_NOT_FOUND", "장바구니에 해당 강의가 존재하지 않습니다."),
    CART_LECTURE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "CART_LECTURE_LIMIT_EXCEEDED", "장바구니에 담을 수 있는 최대 강의 수를 초과했습니다."),

    // 서버/DB 관련
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "서버 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DATABASE_ERROR", "데이터베이스 처리 중 오류가 발생했습니다.");



    private final HttpStatus status;
    private final String code;
    private final String message;
}