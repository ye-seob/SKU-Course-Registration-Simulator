package com.v1.skuproject.service;

import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class LectureRankingService {
    private final LectureRepository lectureRepository;

    private List<Lecture> ranking = new ArrayList<>();

    /**
     * rating + 과목 타입 기준으로 순위 생성
     */
    public void buildRanking() {
        List<Lecture> lectures =  lectureRepository.findAll();

        this.ranking = lectures.stream()
                .sorted(Comparator.comparingDouble(this::getScore).reversed())
                .toList();

        int rank = 1;

        for (Lecture lecture : ranking) {
            log.info(
                    "{}위 | {} | 타입: {} | 평점: {} | 점수: {}",
                    rank,
                    lecture.getLectureName(),
                    lecture.getType(),
                    lecture.getRating(),
                    String.format("%.2f", getScore(lecture))
            );
            rank++;
        }

        log.info("=== 순위 출력 종료 (총 {}개) ===", ranking.size());

    }


    public Lecture pickLecture() {
        // 정원이 찬 강의는 제외
        List<Lecture> available = ranking.stream()
                .filter(l -> l.getEnrollment() < l.getCapacity())
                .toList();

        if (available.isEmpty()) {
            throw new IllegalStateException("신청 가능한 강의가 없습니다.");
        }

        int size = available.size();

        // 랜덤 선택 (30%)
        if (Math.random() < 0.3) {
            return available.get(
                    ThreadLocalRandom.current().nextInt(size)
            );
        }

        // 3. 인기 기반 선택 (70%)
        double randomValue = Math.random();

        // 인기 과목 쏠림 방지
        double biasedValue = Math.pow(randomValue, 1.5);

        int selectedIndex = (int) (biasedValue * size);

        return available.get(selectedIndex);
    }


    private double getScore(Lecture lecture) {
        double base;

        LectureType type = lecture.getType();

        if (type == LectureType.전핵 || type == LectureType.교선) {
            base = 100;
        } else if (type == LectureType.전선 || type == LectureType.교필) {
            base = 60;
        } else {
            base = 30;
        }

        return base + lecture.getRating();
    }
}