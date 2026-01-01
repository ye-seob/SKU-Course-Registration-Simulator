package com.v1.skuproject.service;

import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.domain.user.User;
import com.v1.skuproject.repository.LectureRepository;
import com.v1.skuproject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class EnrollmentServiceTest {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void  enrollmentTest() throws Exception {
        int threadCount = 10;

        // --------------------------
        // 1. 테스트용 강의 생성
        // --------------------------
        Lecture lecture = Lecture.builder()
                .professor("테스트 test 교수")
                .lectureName("동시성 테스트 강의")
                .lectureCode("TEST101")
                .classNumber(1)
                .type(LectureType.전핵)
                .credit(3)
                .time(3)
                .capacity(1)        // 정원 1
                .enrollment(0)
                .rating(0)
                .gradingMethod("절대평가")
                .schedule("월1,수1")
                .major(Major.소프트웨어학과)
                .build();

        lectureRepository.saveAndFlush(lecture);

        // --------------------------
        // 2. 테스트용 유저 생성
        // --------------------------
        List<User> users = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            User user = User.builder()
                    .studentId(20213000 + i + 1)
                    .name("테스트유저" + (i + 1))
                    .password("password")
                    .major(Major.소프트웨어학과)
                    .grade(2)
                    .maxCredit(18)
                    .build();
            users.add(userRepository.saveAndFlush(user));
        }

        // --------------------------
        // 3. 동시성 테스트
        // --------------------------
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final Long userId = users.get(i).getId();
            executorService.submit(() -> {
                try {
                    startLatch.await();
                    enrollmentService.enroll(userId, lecture.getId());
                } catch (Exception e) {

                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await();
    }
}