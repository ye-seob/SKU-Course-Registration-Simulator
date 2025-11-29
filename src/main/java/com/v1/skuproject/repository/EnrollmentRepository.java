package com.v1.skuproject.repository;

import com.v1.skuproject.domain.enrollment.Enrollment;
import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // 사용자 수강 신청 내역 조회
    List<Enrollment> findAllByUser(User user);

    // 사용자-강좌 존재 여부 확인
    Optional<Enrollment> findByUserAndLecture(User user, Lecture lecture);

    // 삭제 메서드
    void deleteByUserAndLecture(User user, Lecture lecture);
}