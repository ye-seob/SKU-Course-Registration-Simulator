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
    private final LectureRankingService lectureRankingService;



    // 55분 - 초기화
    @Scheduled(cron = "0 55 * * * *")
    public void resetEnrollment() {
        log.info("수강신청 초기화 시작");

        enrollmentService.resetEnrollmentStatuses(); // 신청 내역 초기화
        queueService.clearAllQueues();               // 대기열 초기화
        enrollmentService.resetLectureCounts();      // 신청 인원 초기화

        // rating + 타입 기준으로 순위 생성
        lectureRankingService.buildRanking();

        log.info("수강신청 초기화 완료");
    }
}