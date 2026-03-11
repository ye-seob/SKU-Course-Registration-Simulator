package com.v1.skuproject.simulation;

import com.v1.skuproject.queue.service.QueueService;
import com.v1.skuproject.simulation.entity.DummyUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrafficGenerator {

    private final QueueService queueService;
    private final DummyUserFactory dummyUserFactory;


    public void generateInitialUsers(int count, SimulationScheduler scheduler) {
        List<DummyUser> users = dummyUserFactory.create(count);
        for (DummyUser user : users) {
            scheduler.execute(() -> queueService.enter(user.getId(), user.getTargetLectureId()));
        }
        log.info("[시작 직후] {}명 즉시 투입", count);
    }

    public void generateContinuousUsers(long simulationStartTime, boolean isRunning, SimulationScheduler scheduler) {
        if (!isRunning) return;

        long elapsedSec = (System.currentTimeMillis() - simulationStartTime) / 1000;
        int count = (elapsedSec <= 180) ? random(35, 50) : random(20, 30);

        List<DummyUser> users = dummyUserFactory.create(count);
        for (DummyUser user : users) {
            scheduler.execute(() -> queueService.enter(user.getId(), user.getTargetLectureId()));
        }
    }

    private int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
