package com.v1.skuproject.queue.notifier;

import com.v1.skuproject.queue.dto.QueueRankResponse;
import com.v1.skuproject.queue.dto.QueueResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueNotifier {
    private final SimpMessagingTemplate messagingTemplate;
    public void sendRank(Long userId, QueueRankResponse rank) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue-rank",
                rank
        );
    }

    public void sendSuccess(Long userId, String message) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue-end",
                QueueResultResponse.success(message)
        );
    }

    public void sendFail(Long userId, String message) {
        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue-end",
                QueueResultResponse.fail(message)
        );
    }

}
