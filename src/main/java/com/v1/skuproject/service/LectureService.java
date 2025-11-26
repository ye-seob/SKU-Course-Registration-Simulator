package com.v1.skuproject.service;

import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.repository.LectureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LectureService {

    private final LectureRepository lectureRepository;

    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }


    public List<Lecture> searchLectures(Major major, LectureType type, String keyword) {
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

        return lectures.stream()
                .map(l -> Lecture.builder()
                        .id(l.getId())
                        .lectureName(l.getLectureName())
                        .enrollment(l.getEnrollment())
                        .major(l.getMajor())
                        .type(l.getType())
                        .capacity(l.getCapacity())
                        .build())
                .collect(Collectors.toList());
    }
}