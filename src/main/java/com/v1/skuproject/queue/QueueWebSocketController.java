package com.v1.skuproject.queue;

import com.v1.skuproject.dto.queue.QueueRankResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;


/**
 * WS 기반 수강신청 대기열 컨트롤러
 *
 * WS 구독 시 대기열 등록 및 현재 순번 전송
 * WS disconnect 시 대기열 자동 제거
 */
@Controller
@RequiredArgsConstructor
@Tag(name = "Queue-WS", description = "수강신청 대기열 WS")
public class QueueWebSocketController {

    private final QueueService queueService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/enter/{lectureId}")
    public void enterQueue(
            @DestinationVariable("lectureId") Long lectureId,
            Principal principal) {

        Long userId = Long.valueOf(principal.getName());

        // 대기열에 사용자 등록
        queueService.enter(userId, lectureId);

        // 현재 순번 조회
        QueueRankResponse rank = queueService.getRank(userId, lectureId);

        // 해당 사용자에게 순번 전송
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue-rank",
                rank
        );

    }
}
