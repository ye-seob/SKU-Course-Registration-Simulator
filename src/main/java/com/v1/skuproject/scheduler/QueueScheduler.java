package com.v1.skuproject.scheduler;

import com.v1.skuproject.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Redis 기반 수강신청 대기열을 주기적으로 처리하는 스케줄러
 */
@Component
@RequiredArgsConstructor
public class QueueScheduler {

    private final QueueService queueService;

    @Scheduled(fixedDelay = 1000)
    public void processQueue() {
        queueService.processQueue();
    }
}