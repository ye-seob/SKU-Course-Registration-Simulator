package com.v1.skuproject.enrollment.repository;

import com.v1.skuproject.enrollment.entity.Enrollment;
import com.v1.skuproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {


    List<Enrollment> findAllByUser(User user);

    Optional<Enrollment> findByUser_IdAndLecture_Id(Long userId, Long lectureId);

    @Modifying
    @Query("""
            DELETE FROM Enrollment e
            WHERE e.user.role = 'ROLE_GUEST'
            AND e.user.createdAt < :time
            """)
    int deleteGuestEnrollments(@Param("time") LocalDateTime time);

}