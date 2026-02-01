package com.v1.skuproject.simulation;

import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.repository.LectureRepository;
import com.v1.skuproject.service.LectureRankingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimulationService {

    private static final String QUEUE_KEY = "enrollment:queue";

    private final LectureRepository lectureRepository;
    private final StringRedisTemplate redisTemplate;
    private final LectureRankingService lectureRankingService;
    /**
     long이면 동시성 문제 발생 가능
     */
    private final AtomicLong userIdSeq = new AtomicLong(0);
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    /**
     boolean이면 동시성 문제 발생 가능
     */
    private volatile boolean isRunning = false;

    private long simulationStartTime;

    public void startSimulation() {
        if (isRunning) {
            log.warn("이미 시뮬레이션이 실행 중입니다.");
            return;
        }

        if (scheduler.isShutdown()) {
            createScheduler(); // 스레드  생성
        }



        isRunning = true;
        simulationStartTime = System.currentTimeMillis();
        log.info("=== 더미 유저 시뮬레이션 시작 ===");

        addInitialDummy();

        scheduler.scheduleAtFixedRate(this::addContinuousDummy, 0, 1, TimeUnit.SECONDS);

        /**
         * 30분 후 자동 종료
         */
        scheduler.schedule(this::stopSimulation, 20, TimeUnit.MINUTES);
    }

    public void createScheduler() {
        scheduler = Executors.newScheduledThreadPool(10);
        log.info("새 스레줄러 생성 완료");
    }



    public void stopSimulation() {
        isRunning = false;
        log.info("=== 시뮬레이션 종료 ===");
    }

    public void shutdownScheduler() {
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

    /**
     * 시작 직후 대량 투입
     *  지연 없이 즉시 실행 위해 schedule이 아닌 execute 사용
     */
    private void addInitialDummy() {
        List<DummyUser> users = createDummyUsers(500);

        for (DummyUser user : users) {
            scheduler.execute(() -> enterDummyUser(user));
        }

        log.info("[시작 직후] 500명 즉시 투입");
    }

    /**
     * 초 단위 지속 유입
     */
    private void addContinuousDummy() {
        if (!isRunning) {
            return;
        }

        long elapsedSec =
                (System.currentTimeMillis() - simulationStartTime) / 1000;

        int count;

        if (elapsedSec <= 60) {
            count = random(30, 40);
        } else if (elapsedSec <= 1200) {
            count = random(20, 25);
        } else if (elapsedSec <= 1800) {
            count = random(10, 15);
        }  else {
           count = random(5, 8);
        }
        log.info("초당 유입: {}", count);

        List<DummyUser> users = createDummyUsers(count);

        for (DummyUser user : users) {
            enterDummyUser(user);
        }
    }

    private int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private List<DummyUser> createDummyUsers(int count) {
        List<Lecture> lectures = lectureRepository.findAll();

        if (lectures.isEmpty()) {
            throw new IllegalStateException("강의가 존재하지 않습니다.");
        }

        List<DummyUser> users = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            long userId = userIdSeq.incrementAndGet();

            Lecture lecture = lectureRankingService.pickLecture();
            users.add(DummyUser.create(userId, lecture.getId()));
        }

        /**
         * 생성 순서가 곧 요청 순서가 되지 않도록 셔플
         */
        Collections.shuffle(users);
        return users;
    }

    /**
     *  더미 유저 대기열 등록
     */
    private void enterDummyUser(DummyUser user) {
        long score =
                System.currentTimeMillis()
                        + ThreadLocalRandom.current().nextLong(0, 50);

        redisTemplate.opsForZSet()
                .add(
                        QUEUE_KEY,
                        user.getId() + ":" + user.getTargetLectureId(),
                        (double) score
                );
    }
}