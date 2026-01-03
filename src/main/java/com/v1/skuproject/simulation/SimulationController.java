package com.v1.skuproject.simulation;

import com.v1.skuproject.queue.QueueService;
import com.v1.skuproject.service.EnrollmentService;
import com.v1.skuproject.util.response.ApiResponse;
import com.v1.skuproject.util.response.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<String>> startSimulation() {
        log.info("=== [HTTP 트리거] 시뮬레이션 시작 요청 ===");
        enrollmentService.openEnrollment();
        try {
            simulationService.startSimulation();

            return ResponseHandler.ok(
                    "시뮬레이션이 시작"
            );

        } catch (Exception e) {
            log.error("시뮬레이션 실행 중 오류 발생", e);

            return ResponseHandler.error(
                    "시뮬레이션 실행 실패",
                    e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );

        }
    }


    @PostMapping("/reset")
    public void resetAll() {
        log.info("수강신청 초기화 시작");

        enrollmentService.resetEnrollmentStatuses();
        queueService.clearAllQueues();
        enrollmentService.resetLectureCounts();

        log.info("수강신청 초기화 완료");
    }

}