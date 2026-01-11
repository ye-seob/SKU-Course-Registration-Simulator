package com.v1.skuproject.service;

import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
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

    /**
     * 강의 검색
     */
    public List<Lecture> searchLectures(Major major, LectureType type, String keyword) {

        log.debug("강의 검색 조건 major={}, type={}, keyword={}", major, type, keyword);

        List<Lecture> lectures = lectureRepository.findAll((root, query, cb) -> {
            var predicates = cb.conjunction();

            if (major != null) {
                predicates = cb.and(predicates, cb.equal(root.get("major"), major));
            }

            if (type != null) {
                predicates = cb.and(predicates, cb.equal(root.get("type"), type));
            }

            if (keyword != null && !keyword.isBlank()) {
                predicates = cb.and(
                        predicates,
                        cb.like(root.get("lectureName"), "%" + keyword + "%")
                );
            }

            return predicates;
        });


        return lectures.stream()
                .map(l -> Lecture.builder()
                        .id(l.getId())
                        .lectureName(l.getLectureName())
                        .enrollment(l.getEnrollment())
                        .capacity(l.getCapacity())
                        .major(l.getMajor())
                        .classNumber(l.getClassNumber())
                        .time(l.getTime())
                        .professor(l.getProfessor())
                        .schedule(l.getSchedule())
                        .rating(l.getRating())
                        .gradingMethod(l.getGradingMethod())
                        .lectureCode(l.getLectureCode())
                        .type(l.getType())
                        .room(l.getRoom())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 강의 평점 등록 / 수정
     */
    @Transactional
    public void rateLecture(Long userId, Long lectureId, int score) {

        // 평점 범위 검증
        if (score < 1 || score > 5) {
            log.warn("강의 평점 등록 실패 - 점수 범위 오류 userId={} lectureId={} score={}",
                    userId, lectureId, score);
            throw new BaseException(ErrorCode.LECTURE_RATING_INVALID_SCORE);
        }

        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        // 기존 평점 조회
        LectureRating existingRating = lecture.findRatingByUserId(userId);

        if (existingRating != null) {
            int oldScore = existingRating.getScore();
            existingRating.updateScore(score);
            lecture.updateRating(oldScore, score);

            log.info("강의 평점 수정 userId={} lectureId={} {}→{}",
                    userId, lectureId, oldScore, score);
        } else {
            lecture.getRatings().add(new LectureRating(lecture, userId, score));
            lecture.addRating(score);

            log.info("강의 평점 등록 userId={} lectureId={} score={}",
                    userId, lectureId, score);
        }
    }
}