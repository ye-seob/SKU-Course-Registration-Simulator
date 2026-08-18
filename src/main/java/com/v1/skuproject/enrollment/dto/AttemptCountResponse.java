package com.v1.skuproject.enrollment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AttemptCountResponse {

    private final long count;

    public static AttemptCountResponse of(long count) {
        return new AttemptCountResponse(count);
    }
}