package com.v1.skuproject.util;

import com.v1.skuproject.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
public class QueueScheduler {

    private static final String QUEUE_KEY = "enrollment:queue";
    private final StringRedisTemplate redisTemplate;
    private final EnrollmentService enrollmentService;

    @Scheduled(fixedDelay = 1000)
    public void processQueue() {
        // 10~50명
        int processCount = ThreadLocalRandom.current().nextInt(10, 50);

        Set<ZSetOperations.TypedTuple<String>> poppedValues =
                redisTemplate.opsForZSet().popMin(QUEUE_KEY, processCount);

        if (poppedValues == null || poppedValues.isEmpty()) {
            return;
        }

        for (ZSetOperations.TypedTuple<String> tuple : poppedValues) {
            String value = tuple.getValue();
            if (value == null) continue;

            String[] data = value.split(":");

            if("dummy".equals(data[0])){
               continue;
            }

            Long userId = Long.parseLong(data[0]);
            Long lectureId = Long.parseLong(data[1]);


            try {
                enrollmentService.enroll(userId, lectureId);
                log.info("[처리완료] 유저: " + userId + ", 강의: " + lectureId);
            } catch (Exception e) {
                log.error("[처리실패] 유저: " + userId + " - " + e.getMessage());
            }
        }
    }
}
