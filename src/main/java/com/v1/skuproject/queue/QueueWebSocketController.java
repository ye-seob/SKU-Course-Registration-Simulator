package com.v1.skuproject.queue;

import com.v1.skuproject.dto.queue.QueueRankResponse;
import com.v1.skuproject.dto.queue.QueueResultResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "Queue-WS", description = "수강신청 대기열 WS")
public class QueueWebSocketController {

    private final QueueService queueService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/enter/{lectureId}")
    public void enterQueue(
            @DestinationVariable("lectureId") Long lectureId,
            Principal principal
    ) {
        if (principal == null) {
            log.warn("WS 대기열 진입 실패 - 인증 정보 없음");
            return;
        }

        Long userId;
        try {
            userId = Long.valueOf(principal.getName());
        } catch (NumberFormatException e) {
            log.warn("WS 대기열 진입 실패 - userId 파싱 오류 value={}", principal.getName());
            return;
        }

        try {
            // 대기열 등록
            queueService.enter(userId, lectureId);

            // 현재 순번 조회
            QueueRankResponse rank = queueService.getRank(userId, lectureId);

            // 순번 전송
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue-rank",
                    rank
            );

            log.info("WS 대기열 진입 성공 userId={} lectureId={}", userId, lectureId);

        } catch (IllegalStateException e) {
            //오류 (대기열 불일치 등)
            log.warn(
                    "WS 대기열 진입 실패 userId={} lectureId={} 사유={}",
                    userId, lectureId, e.getMessage()
            );

            sendFailMessage(userId, "대기열 처리 중 문제가 발생했습니다.");

        } catch (Exception e) {
            // 시스템 오류
            log.error(
                    "WS 대기열 진입 중 시스템 오류 userId={} lectureId={}",
                    userId, lectureId, e
            );

            sendFailMessage(userId, "서버 오류로 대기열 진입에 실패했습니다.");

        }
    }

    private void sendFailMessage(Long userId, String message) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue-end",
                QueueResultResponse.builder()
                        .status("FAIL")
                        .message(message)
                        .build()
        );
    }
}