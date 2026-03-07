package com.v1.skuproject.queue;

import com.v1.skuproject.config.security.UserPrincipal;
import com.v1.skuproject.dto.queue.QueueRankResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class QueueService {

    /**
     * Redis ZSet에 저장될 대기열 키
     */
    private static final String QUEUE_KEY = "enrollment:queue";

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * WS 구독자 관리
     * key   : userId
     * value : lectureId
     */
    private final Map<Long, Long> subscribers = new ConcurrentHashMap<>();

    /**
     * 1초마다 모든 구독자에게 현재 대기열 순번 Push
     */
    @Scheduled(fixedDelay = 1000)
    public void pushQueueUpdate() {
        for (Map.Entry<Long, Long> entry : subscribers.entrySet()) {
            Long userId = entry.getKey();
            Long lectureId = entry.getValue();

            try {
                QueueRankResponse rank = getRank(userId, lectureId);

                messagingTemplate.convertAndSendToUser(
                        userId.toString(),
                        "/queue-rank",
                        rank
                );

            } catch (IllegalStateException e) {
                // 대기열 정보 불일치 등 문제
                log.warn(
                        "대기열 순번 조회 실패 userId={} lectureId={} 사유={}",
                        userId, lectureId, e.getMessage()
                );
            } catch (Exception e) {
                // Redis / WS 등 시스템 문제
                log.error(
                        "대기열 순번 전송 중 시스템 오류 userId={} lectureId={}",
                        userId, lectureId, e
                );
            }
        }
    }

    /**
     * 대기열 등록
     */
    public void enter(Long userId, Long lectureId) {
        String value = generateValue(userId, lectureId);
        long now = System.currentTimeMillis();

        try {
            redisTemplate.opsForZSet()
                    .add(QUEUE_KEY, value, (double) now);

            subscribers.put(userId, lectureId);


            // 즉시 한 번 알림 전송
            notifyQueueUpdate();

        } catch (Exception e) {
            log.error(
                    "대기열 등록 실패 userId={} lectureId={}",
                    userId, lectureId, e
            );
            throw e;
        }
    }

    /**
     * 현재 사용자의 대기열 순번 조회
     */
    public QueueRankResponse getRank(Long userId, Long lectureId) {
        String value = generateValue(userId, lectureId);

        Long rank = redisTemplate.opsForZSet().rank(QUEUE_KEY, value);
        Long totalSize = redisTemplate.opsForZSet().zCard(QUEUE_KEY);

        if (rank == null || totalSize == null) {
            throw new IllegalStateException("대기열 정보가 존재하지 않음");
        }

        Long aheadCount = rank;
        Long behindCount = totalSize - rank - 1;

        return QueueRankResponse.builder()
                .aheadCount(aheadCount)
                .behindCount(behindCount)
                .build();
    }

    /**
     * 대기열 이탈
     */
    public void exit(Long userId, Long lectureId) {
        String value = generateValue(userId, lectureId);

        try {
            redisTemplate.opsForZSet().remove(QUEUE_KEY, value);
            subscribers.remove(userId);


        } catch (Exception e) {
            log.error(
                    "대기열 이탈 처리 실패 userId={} lectureId={}",
                    userId, lectureId, e
            );
        }
    }

    /**
     * WebSocket 연결 종료 시 자동 호출
     */
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        Authentication authentication =
                (Authentication) event.getUser();

        if (authentication == null) return;

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        Long userId = principal.getUserId();

        Long lectureId = subscribers.get(userId);
        if (lectureId == null) {
            return;
        }

        String value = generateValue(userId, lectureId);

        redisTemplate.opsForZSet().remove(QUEUE_KEY, value);
        subscribers.remove(userId);

        log.info(
                "WS 종료로 인한 대기열 자동 이탈 userId={} lectureId={}",
                userId, lectureId
        );
    }

    /**
     * Redis ZSet value 생성
     * userId:lectureId
     */
    private String generateValue(Long userId, Long lectureId) {
        return userId + ":" + lectureId;
    }

    /**
     * 현재 모든 구독자에게 대기열 상태 즉시 전송
     */
    private void notifyQueueUpdate() {
        for (Map.Entry<Long, Long> entry : subscribers.entrySet()) {
            Long userId = entry.getKey();
            Long lectureId = entry.getValue();

            try {
                QueueRankResponse rank = getRank(userId, lectureId);

                messagingTemplate.convertAndSendToUser(
                        userId.toString(),
                        "/queue-rank",
                        rank
                );

            } catch (Exception e) {
                log.error(
                        "대기열 즉시 알림 실패 userId={} lectureId={}",
                        userId, lectureId, e
                );
            }
        }
    }

    /**
     * 대기열 전체 초기화
     */
    public void clearAllQueues() {
        log.info("대기열 전체 초기화 시작");

        try {
            redisTemplate.delete(QUEUE_KEY);
            subscribers.clear();
        } catch (Exception e) {
            log.error("대기열 전체 초기화 실패", e);
            throw e;
        }

        log.info("대기열 전체 초기화 완료");
    }
}