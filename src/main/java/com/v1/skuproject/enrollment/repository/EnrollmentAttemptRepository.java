package com.v1.skuproject.enrollment.repository;

import com.v1.skuproject.enrollment.entity.EnrollmentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentAttemptRepository extends JpaRepository<EnrollmentAttempt,Long> {
}
