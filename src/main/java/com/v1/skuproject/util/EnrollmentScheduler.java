package com.v1.skuproject.util;

import com.v1.skuproject.queue.QueueService;
import com.v1.skuproject.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class EnrollmentScheduler {

    private final EnrollmentService enrollmentService;
    private final QueueService queueService;

    // 00분 - 수강신청 오픈
    @Scheduled(cron = "0 0 * * * *")
    public void openEnrollment() {
        log.info("수강신청 오픈");
        enrollmentService.openEnrollment(); // 상태 플래그 ON
    }

    // 50분 - 신청 마감
    @Scheduled(cron = "0 50 * * * *")
    public void closeEnrollment() {
        log.info("수강신청 마감");
        enrollmentService.closeEnrollment(); // 상태 플래그 OFF
    }

    // 55분 - 초기화
    @Scheduled(cron = "0 55 * * * *")
    public void resetEnrollment() {
        log.info("수강신청 초기화 시작");

        enrollmentService.resetEnrollmentStatuses(); // 신청 내역 초기화
        queueService.clearAllQueues();               // 대기열 초기화
        enrollmentService.resetLectureCounts();      // 신청 인원 초기화

        log.info("수강신청 초기화 완료");
    }
}