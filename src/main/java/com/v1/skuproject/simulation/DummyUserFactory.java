package com.v1.skuproject.simulation;

import com.v1.skuproject.lecture.entity.Lecture;
import com.v1.skuproject.lecture.service.LectureRankingService;
import com.v1.skuproject.simulation.entity.DummyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
public class DummyUserFactory {

    private final LectureRankingService lectureRankingService;
    private final AtomicLong userIdSeq = new AtomicLong();

    public List<DummyUser> create(int count) {

        List<DummyUser> users = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            long userId = userIdSeq.incrementAndGet();
            Lecture lecture = lectureRankingService.pickLecture();

            users.add(DummyUser.create(userId, lecture.getId()));
        }

        return users;
    }
}