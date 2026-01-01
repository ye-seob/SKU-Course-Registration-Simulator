package com.v1.skuproject.queue;

import com.v1.skuproject.dto.queue.QueueRankResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
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
     * 1초마다 모든 구독자에게 현재 대기열 순번을 Push
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
            } catch (Exception e) {
                log.error("대기열 업데이트 실패 - userId={}, lectureId={}", userId, lectureId, e);
            }
        }
    }

    /**
     * 대기열 등록
     */
    public void enter(Long userId, Long lectureId) {
        String value = generateValue(userId, lectureId);
        long now = System.currentTimeMillis();

        // 더미 데이터 삽입 (테스트용)
        int dummyNum = (int) (Math.random() * 201) + 100;
        for (int i = 0; i < dummyNum; i++) {
            String dummyValue = "dummy:" + i;
            redisTemplate.opsForZSet()
                    .add(QUEUE_KEY, dummyValue, (double) (now - (i * 100L)));
        }
        for (int i = 9999; i < dummyNum+9999; i++) {
            String dummyValue = "dummy:" + i;
            redisTemplate.opsForZSet()
                    .add(QUEUE_KEY, dummyValue, (double) (now + (i * 100L)));
        }

        // 실제 사용자 대기열 등록
        redisTemplate.opsForZSet()
                .add(QUEUE_KEY, value, (double) now);

        // 구독자 등록 (userId -> lectureId)
        subscribers.put(userId, lectureId);

        // 즉시 한 번 알림 전송
        notifyQueueUpdate();
    }

    /**
     * 현재 사용자의 대기열 순번 조회
     */
    public QueueRankResponse getRank(Long userId, Long lectureId) {
        String value = generateValue(userId, lectureId);

        Long rank = redisTemplate.opsForZSet().rank(QUEUE_KEY, value);
        Long totalSize = redisTemplate.opsForZSet().zCard(QUEUE_KEY);

        if (rank == null || totalSize == null) {
            throw new RuntimeException("대기열 조회 실패");
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

        redisTemplate.opsForZSet().remove(QUEUE_KEY, value);
        subscribers.remove(userId);
    }
    /**
     * WebSocket 연결 종료 시 자동 호출
     **/
    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event){
        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        Principal principal = accessor.getUser();

        if (principal == null) {
            return;
        }

        Long userId;
        try {
            userId = Long.valueOf(principal.getName());
        } catch (NumberFormatException e) {
            log.warn("WS disconnect - userId 파싱 실패: {}", principal.getName());
            return;
        }

        Long lectureId = subscribers.get(userId);
        if (lectureId == null) {
            return;
        }

        String value = generateValue(userId, lectureId);

        redisTemplate.opsForZSet().remove(QUEUE_KEY, value);
        subscribers.remove(userId);

        log.info("WS 종료 → 대기열 취소 처리 userId={}, lectureId={}",
                userId, lectureId);
    }



    /**
     * Redis ZSet에 저장될 value 생성
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
                log.error("즉시 알림 실패 - userId={}, lectureId={}", userId, lectureId, e);
            }
        }
    }
}