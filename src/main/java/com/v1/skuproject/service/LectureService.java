package com.v1.skuproject.service;

import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.lecture.LectureRating;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.repository.LectureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class LectureService {

    private final LectureRepository lectureRepository;

    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }


    public List<Lecture> searchLectures(Major major, LectureType type, String keyword) {
        log.debug("강의 검색 조건 major={}, type={}, keyword={}", major, type, keyword);

        // Specification을 이용한 동적 쿼리 생성
        List<Lecture> lectures = lectureRepository.findAll((root, query, cb) -> {

            var predicates = cb.conjunction();

            // 전공 조건 추가
            if (major != null) {
                predicates = cb.and(predicates, cb.equal(root.get("major"), major));
            }

            // 타입 조건 추가
            if (type != null) {
                predicates = cb.and(predicates, cb.equal(root.get("type"), type));
            }

            // 키워드 검색 조건 추가
            if (keyword != null && !keyword.isEmpty()) {
                predicates = cb.and(predicates, cb.like(root.get("lectureName"), "%" + keyword + "%"));
            }

            return predicates;
        });

        List<Lecture> result = lectures.stream()
                .map(l -> Lecture.builder()
                        .id(l.getId())
                        .lectureName(l.getLectureName())
                        .enrollment(l.getEnrollment())
                        .major(l.getMajor())
                        .classNumber(l.getClassNumber())
                        .time(l.getTime())
                        .professor(l.getProfessor())
                        .schedule(l.getSchedule())
                        .rating(l.getRating())
                        .gradingMethod(l.getGradingMethod())
                        .lectureCode(l.getLectureCode())
                        .type(l.getType())
                        .capacity(l.getCapacity())
                        .build())
                .collect(Collectors.toList());

        return result;
    }

    @Transactional
    public void rateLecture(Long userId, Long lectureId, int score) {

        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("평점은 1에서 5 사이여야 합니다.");
        }

        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("강의가 존재하지 않습니다."));

        // 등록한 평점이 있는지 확인
        LectureRating existingRating = lecture.findRatingByUserId(userId);

        // 있으면 업데이트
        if (existingRating != null) {
            int oldScore = existingRating.getScore();
            existingRating.updateScore(score);
            lecture.updateRating(oldScore, score);
        } else {  // 없으면 새로 생성
            lecture.getRatings().add(new LectureRating(lecture, userId, score));
            lecture.addRating(score);
        }
    }
}