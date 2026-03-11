package com.v1.skuproject.queue.controller;

import com.v1.skuproject.config.security.UserPrincipal;
import com.v1.skuproject.queue.service.QueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
@Slf4j
public class QueueController {

    private final QueueService queueService;


    @MessageMapping("/enter/{lectureId}")
    public void enterQueue(
            @DestinationVariable("lectureId") Long lectureId,
            Principal principal
    ) {
        Authentication authentication = (Authentication) principal;
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();

        Long userId = user.getUserId();

        // 대기열 등록
        queueService.enter(userId, lectureId);


        log.info("WS 대기열 진입 성공 userId={} lectureId={}", userId, lectureId);

    }

}