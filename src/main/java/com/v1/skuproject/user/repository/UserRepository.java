package com.v1.skuproject.user.repository;

import com.v1.skuproject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByStudentId(String studentId);
    Boolean existsByStudentId(String studentId);

    @Modifying
    @Query("""
        DELETE FROM User u
        WHERE u.role = 'ROLE_GUEST'
        AND u.createdAt < :time
    """)
    int deleteGuestUsers(@Param("time") LocalDateTime time);


}
