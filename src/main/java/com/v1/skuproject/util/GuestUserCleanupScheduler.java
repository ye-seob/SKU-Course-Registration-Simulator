package com.v1.skuproject.util;

import com.v1.skuproject.service.EnrollmentService;
import com.v1.skuproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GuestUserCleanupScheduler {

    private final EnrollmentService enrollmentService;
    private final UserService userService;

    @Scheduled(cron = "0 55 * * * *")
    public void deleteGuestUsers() {
        log.info("1시간이 지난 비회원 유저 삭제 시작");

        enrollmentService.deleteGuestEnrollment();

        userService.deleteGuestUsers();

        log.info("1시간이 지난 비회원 유저 삭제 완료");
    }
}