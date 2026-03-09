package com.v1.skuproject.queue.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueueResultResponse {

    /**
     * SUCCESS / FAIL
     */
    private String status;

    /**
     * 사용자에게 보여줄 메시지
     */
    private String message;
}