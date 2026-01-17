package com.v1.skuproject.queue;

import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
import com.v1.skuproject.dto.queue.QueueResultResponse;
import com.v1.skuproject.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Redis 기반 수강신청 대기열을 주기적으로 처리하는 스케줄러
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class QueueScheduler {

    private final StringRedisTemplate redisTemplate;
    private final EnrollmentService enrollmentService;
    private final SimpMessagingTemplate messagingTemplate;
    private final QueueService queueService;

    @Scheduled(fixedDelay = 1000)
    public void processQueue() {

        int processCount = ThreadLocalRandom.current().nextInt(10, 100);

        Set<String> values = redisTemplate.opsForZSet()
                .range("enrollment:queue", 0, processCount - 1);

        if (values == null || values.isEmpty()) {
            return;
        }


        for (String value : values) {
            if (value == null) {
                continue;
            }

            String[] data = value.split(":");
            Long userId = Long.parseLong(data[0]);
            Long lectureId = Long.parseLong(data[1]);

            // 더미 유저 처리
            if (userId >= 100_000L) {
                enrollmentService.enrollDummy(lectureId);
                queueService.exit(userId, lectureId);
                continue;
            }

            try {
                enrollmentService.enroll(userId, lectureId);

                messagingTemplate.convertAndSendToUser(
                        userId.toString(),
                        "/queue-end",
                        QueueResultResponse.builder()
                                .status("SUCCESS")
                                .message("수강신청이 완료되었습니다.")
                                .build()
                );

                log.info(
                        "수강신청 처리 성공 userId={} lectureId={}",
                        userId, lectureId
                );

            } catch (BaseException e) {
                ErrorCode errorCode = e.getErrorCode();

                messagingTemplate.convertAndSendToUser(
                        userId.toString(),
                        "/queue-end",
                        QueueResultResponse.builder()
                                .status("FAIL")
                                .message(errorCode.getMessage())
                                .build()
                );

                log.warn(
                        "수강신청 처리 실패 userId={} lectureId={} errorCode={}",
                        userId, lectureId, errorCode.getCode()
                );

            } catch (Exception e) {

                messagingTemplate.convertAndSendToUser(
                        userId.toString(),
                        "/queue-end",
                        QueueResultResponse.builder()
                                .status("FAIL")
                                .message("시스템 오류로 수강신청에 실패했습니다.")
                                .build()
                );

                log.error(
                        "수강신청 처리 실패 userId={} lectureId={}",
                        userId, lectureId, e
                );

            } finally {
                // 대기열 및 구독 해제
                queueService.exit(userId, lectureId);
            }
        }

    }
}