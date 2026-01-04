package com.v1.skuproject.simulation;

import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.repository.LectureRepository;
import com.v1.skuproject.util.LectureRankingService;
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

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(10);

    /**
          long이면 동시성 문제 발생 가능
     */
    private final AtomicLong userIdSeq = new AtomicLong(0);

    /**
       boolean이면 동시성 문제 발생 가능
     */
    private volatile boolean isRunning = false;


    public void startSimulation() {
        if (isRunning) {
            log.warn("이미 시뮬레이션이 실행 중입니다.");
            return;
        }

        isRunning = true;
        log.info("=== 더미 유저 시뮬레이션 시작 ===");


        addInitialDummy();

        /**
         * 초당 30~60명
         */
        addContinuousDummy(0, 3, 30, 60);

        /**
         * 초당 15~35명
         */
        addContinuousDummy(3, 10, 15, 35);

        /**
         * 초당 5~15명
         */
        addContinuousDummy(10, 30, 5, 15);

        /**
         * 30분 후 자동 종료
         */
        scheduler.schedule(this::stopSimulation, 30, TimeUnit.MINUTES);
    }


    public void stopSimulation() {
        isRunning = false;
        log.info("=== 시뮬레이션 종료 ===");
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
     * 초 단위 지속 유입 스케줄링
     *
     * @param startMin       시작 분
     * @param endMin         종료 분
     * @param perSecondMin   초당 최소 유입
     * @param perSecondMax   초당 최대 유입
     *
     */
    private void addContinuousDummy(
            int startMin,
            int endMin,
            int perSecondMin,
            int perSecondMax
    ) {

        long startSec = startMin * 60L;

        scheduler.scheduleAtFixedRate(() -> {
            if (!isRunning) return;

            int count =
                    ThreadLocalRandom.current()
                            .nextInt(perSecondMin, perSecondMax + 1);

            List<DummyUser> users = createDummyUsers(count);

            for (DummyUser user : users) {
                enterDummyUser(user);
            }

            log.debug(
                    "[{}~{}분] 초당 {}명 유입",
                    startMin, endMin, count
            );

        }, startSec, 1, TimeUnit.SECONDS);
        
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