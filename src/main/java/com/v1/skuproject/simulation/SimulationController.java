package com.v1.skuproject.simulation;

import com.v1.skuproject.queue.QueueService;
import com.v1.skuproject.service.EnrollmentService;
import com.v1.skuproject.service.LectureRankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 더미 유저 시뮬레이션 수동 트리거 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/simulation")
@RequiredArgsConstructor
@Slf4j
public class SimulationController {

    private final SimulationService simulationService;
    private final EnrollmentService enrollmentService;
    private final QueueService queueService;
    private final LectureRankingService lectureRankingService;


    @PostMapping("/start")
    public void startSimulation() {
         simulationService.startSimulation();
    }


    @PostMapping("/reset")
    public void resetAll() {
        log.info("수강신청 초기화 시작");

        enrollmentService.resetEnrollmentStatuses();
         simulationService.stopSimulation();
        simulationService.shutdownScheduler();
        simulationService.createScheduler();
        queueService.clearAllQueues();
        enrollmentService.resetLectureCounts();

        lectureRankingService.buildRanking();

        log.info("수강신청 초기화 완료");
    }
}