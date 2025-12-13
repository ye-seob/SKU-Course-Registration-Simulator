package com.v1.skuproject.util.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success; // API 요청 성공 여부(true: 성공, false: 실패)


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data; // 요청 성공 시 여기에 데이터가 포함
    private String code; // 내부적으로 정의된 응답 코드 , HTTP 상태 코드(ex: 200, 400)와는 별개
    private String message; // 응답에 대한 설명 메시지
}
