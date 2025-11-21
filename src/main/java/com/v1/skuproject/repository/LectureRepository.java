package com.v1.skuproject.repository;

import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.domain.user.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {


    // 전공별 조회
    List<Lecture> findByMajor(Major major);


    // 강의 타입별 조회
    List<Lecture> findByType(LectureType type);

    // 전공 + 타입 조회
    List<Lecture> findByMajorAndType(Major major, LectureType type);

    // 이름으로 부분 검색
    List<Lecture> findByLectureNameContaining(String keyword);
}