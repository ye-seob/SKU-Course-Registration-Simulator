package com.v1.skuproject.simulation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SimulationScheduler {

    private ScheduledExecutorService scheduler;

    public void createScheduler(int poolSize) {
        scheduler = Executors.newScheduledThreadPool(poolSize);
        log.info("새 스레줄러 생성 완료");
    }

    public void scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    public void execute(Runnable task) {
        scheduler.execute(task);
    }

    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            log.info("스레줄러 종료 시작");
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            log.info("스레줄러 종료 완료");
        }
    }
}