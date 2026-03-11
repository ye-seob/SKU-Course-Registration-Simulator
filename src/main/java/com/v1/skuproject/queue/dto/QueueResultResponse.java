package com.v1.skuproject.queue.dto;

import com.v1.skuproject.queue.model.QueueResultStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QueueResultResponse {

    private QueueResultStatus status;
    private String message;

    public static QueueResultResponse success(String message) {
        return QueueResultResponse.builder()
                .status(QueueResultStatus.SUCCESS)
                .message(message)
                .build();
    }

    public static QueueResultResponse fail(String message) {
        return QueueResultResponse.builder()
                .status(QueueResultStatus.FAIL)
                .message(message)
                .build();
    }
}