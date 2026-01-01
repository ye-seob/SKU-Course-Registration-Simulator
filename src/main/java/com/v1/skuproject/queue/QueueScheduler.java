package com.v1.skuproject.queue;

import com.v1.skuproject.dto.queue.QueueResultResponse;
import com.v1.skuproject.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * Redis 기반 수강신청 대기열을 일정 주기로 처리하는 스케줄러
 *
 **/
@Component
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
public class QueueScheduler {

    private final StringRedisTemplate redisTemplate; // Redis ZSet 연산용
    private final EnrollmentService enrollmentService; // 실제 수강신청 처리 서비스
    private final SimpMessagingTemplate messagingTemplate;
    private final QueueService queueService;

    @Scheduled(fixedDelay = 1000)
    public void processQueue() {

        // 한 번에 처리할 신청자 수 결정 (랜덤 10~50)
        int processCount = ThreadLocalRandom.current().nextInt(10, 50);

        // Redis ZSet에서 score 기준으로 가장 낮은 값부터 꺼냄
        Set<ZSetOperations.TypedTuple<String>> poppedValues =
                redisTemplate.opsForZSet().popMin("enrollment:queue", processCount);

        // 처리할 값이 없으면 바로 종료
        if (poppedValues == null || poppedValues.isEmpty()) {
            return;
        }

        for (ZSetOperations.TypedTuple<String> tuple : poppedValues) {
            String value = tuple.getValue();
            if (value == null) continue;

            // Redis에 저장된 값 형식: "userId:lectureId"
            String[] data = value.split(":");

            // 더미 데이터는 건너뜀
            if ("dummy".equals(data[0])) continue;

            Long userId = Long.parseLong(data[0]);
            Long lectureId = Long.parseLong(data[1]);

            try {
                // 실제 수강신청 처리
                enrollmentService.enroll(userId, lectureId);

                // 성공 종료 신호 전송
                messagingTemplate.convertAndSendToUser(
                        userId.toString(),
                        "/queue-end",
                        QueueResultResponse.builder()
                                .status("SUCCESS")
                                .message("수강신청이 완료되었습니다.")
                                .build()
                );

                log.info("[처리완료] 유저: {}, 강의: {}", userId, lectureId);

            } catch (Exception e) {

                // 실패 종료 신호 전송
                messagingTemplate.convertAndSendToUser(
                        userId.toString(),
                        "/queue-end",
                        QueueResultResponse.builder()
                                .status("FAIL")
                                .message("수강신청에 실패했습니다.")
                                .build()
                );

                log.error("[처리실패] 유저: {} - {}", userId, e.getMessage());
            } finally {
                // 반드시 대기열 및 구독자 제거
                queueService.exit(userId, lectureId);
            }
        }
    }
}