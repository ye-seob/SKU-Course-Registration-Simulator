package com.v1.skuproject.queue.handler;

import com.v1.skuproject.config.security.UserPrincipal;
import com.v1.skuproject.queue.service.QueueService;
import com.v1.skuproject.queue.service.QueueSubscriberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueDisconnectHandler {

    private final QueueService queueService;
    private final QueueSubscriberService queueSubscriberService;

    /**
     * WebSocket 연결 종료 시 대기열 자동 이탈
     */
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        Authentication authentication = (Authentication) event.getUser();

        if (authentication == null) {
            return;
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUserId();
        Long lectureId = queueSubscriberService.getLecture(userId);

        if (lectureId == null) {
            return;
        }

        queueService.exit(userId, lectureId);
        log.info("WS 종료로 인한 대기열 자동 이탈 userId={} lectureId={}", userId, lectureId);
    }
}