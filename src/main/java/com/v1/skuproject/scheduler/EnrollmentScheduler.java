package com.v1.skuproject.scheduler;

import com.v1.skuproject.enrollment.service.EnrollmentService;
import com.v1.skuproject.lecture.service.LectureRankingService;
import com.v1.skuproject.queue.service.QueueService;
import com.v1.skuproject.simulation.service.SimulationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnrollmentScheduler {

    private final EnrollmentService enrollmentService;
    private final QueueService queueService;
    private final LectureRankingService lectureRankingService;
    private final SimulationService simulationService;

    @Scheduled(cron = "0 0 * * * *") // 매 정각 0분 0초
    public void openEnrollment() {
        simulationService.startSimulation(); // 더미 유저 시뮬레이션 시작
    }

    // 50분 수강신청 종료                                          ㅏㅏㅏㅏㅏ8
    @Scheduled(cron = "0 50 * * * *")
    public void closeEnrollment() {
        log.info("수강신청 종료 시작");

        simulationService.stopSimulation();


        queueService.clearAllQueues();

        enrollmentService.resetEnrollment();;
        enrollmentService.resetLectureCounts();


        log.info("수강신청 종료 완료");
    }

    // 55분 강의 랭킹 빌드
    @Scheduled(cron = "0 55 * * * *")
    public void resetEnrollment() {

        // rating + 타입 기준으로 순위 생성
        lectureRankingService.buildRanking();

        log.info("강의 랭킹 빌드 완료");
    }
}