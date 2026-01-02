package com.v1.skuproject.util;

import com.v1.skuproject.queue.QueueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EnrollmentSchedulerTest {

    @Autowired
    private EnrollmentScheduler scheduler;

    @Autowired
    private QueueService queueService;

    @Test
    void testEnrollmentFlow() {
        // 수강신청 오픈
        scheduler.openEnrollment();

        //  대기열 등록
        queueService.enter(1L, 1L);


        var rank = queueService.getRank(1L, 13L);
        System.out.println("ahead=" + rank.getAheadCount() + ", behind=" + rank.getBehindCount());

        // 수강신청 마감
        scheduler.closeEnrollment();

        // 초기화
        scheduler.resetEnrollment();
    }
}