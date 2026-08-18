package com.v1.skuproject.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserCountResponse {

    private final long count;

    public static UserCountResponse of(long count){
        return new UserCountResponse(count);
    }
}
