package com.v1.skuproject.service;

import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.repository.LectureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LectureService {

    private final LectureRepository lectureRepository;

    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    // 전공별 조회
    public List<Lecture> getLecturesByMajor(Major major) {
        return lectureRepository.findByMajor(major);
    }

    // 타입별 조회
    public List<Lecture> getLecturesByType(LectureType type) {
        return lectureRepository.findByType(type);
    }

    // 전공 + 타입 조회
    public List<Lecture> getLecturesByMajorAndType(Major major, LectureType type) {
        return lectureRepository.findByMajorAndType(major, type);
    }

    // 강의명 검색
    public List<Lecture> searchLecturesByName(String keyword) {
        return lectureRepository.findByLectureNameContaining(keyword);
    }
}