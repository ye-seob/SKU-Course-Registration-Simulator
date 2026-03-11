package com.v1.skuproject.simulation.service;

import com.v1.skuproject.simulation.SimulationScheduler;
import com.v1.skuproject.simulation.TrafficGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimulationService {

    private final TrafficGenerator trafficGenerator;
    private final SimulationScheduler scheduler;

    private volatile boolean isRunning = false;

    private long simulationStartTime;

    public void startSimulation() {
        if (isRunning) {
            log.warn("이미 시뮬레이션 실행 중입니다.");
            return;
        }

        scheduler.createScheduler(10);
        isRunning = true;
        simulationStartTime = System.currentTimeMillis();

        log.info("=== 더미 유저 시뮬레이션 시작 ===");

        // 초기 대량 투입
        trafficGenerator.generateInitialUsers(500, scheduler);

        // 초 단위 지속 유입
        scheduler.scheduleAtFixedRate(() ->
                        trafficGenerator.generateContinuousUsers(simulationStartTime, isRunning,scheduler),
                0, 1, TimeUnit.SECONDS);

    }

    public void stopSimulation() {
        isRunning = false;
        log.info("=== 시뮬레이션 종료 ===");
        scheduler.shutdown();
    }
}