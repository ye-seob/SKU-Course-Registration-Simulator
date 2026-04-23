package com.v1.skuproject.practice.repository;

import com.v1.skuproject.practice.entity.PracticeEnrollment;
import com.v1.skuproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PracticeEnrollmentRepository extends JpaRepository<PracticeEnrollment, Long> {

    List<PracticeEnrollment> findAllByUser(User user);

    boolean existsByUserAndLecture_Id(User user, Long lectureId);

    Optional<PracticeEnrollment> findByUserAndLecture_Id(User user , Long lectureId);
}