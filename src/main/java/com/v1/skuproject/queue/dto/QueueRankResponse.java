package com.v1.skuproject.queue.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class QueueRankResponse {
    private Long aheadCount;  // 내 앞에 있는 사람 수
    private Long behindCount;// 내 뒤에 있는 사람 수
}
