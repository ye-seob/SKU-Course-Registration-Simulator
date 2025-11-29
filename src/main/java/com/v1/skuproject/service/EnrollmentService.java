package com.v1.skuproject.service;

import com.v1.skuproject.domain.enrollment.Enrollment;
import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.user.User;
import com.v1.skuproject.repository.EnrollmentRepository;
import com.v1.skuproject.repository.LectureRepository;
import com.v1.skuproject.repository.UserRepository;
import com.v1.skuproject.util.exception.BaseException;
import com.v1.skuproject.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;

    // 수강 신청
    @Transactional
    public boolean enroll(Long userId, Long lectureId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        // 이미 신청했는지 확인
        Optional<Enrollment> existing = enrollmentRepository.findByUserAndLecture(user, lecture);
        if (existing.isPresent()) {
            throw new BaseException(ErrorCode.INVALID_REQUEST); // 중복 신청
        }

        // 정원 확인
        if (lecture.getEnrollment() >= lecture.getCapacity()) {
            throw new BaseException(ErrorCode.CAPACITY_EXCEEDED);
        }

        // 수강 신청
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .lecture(lecture)
                .build();
        enrollmentRepository.save(enrollment);

        lecture.incrementEnrollment();
        lectureRepository.save(lecture);

        return true;
    }

    // 수강 취소
    @Transactional
    public boolean cancel(Long userId, Long lectureId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        Optional<Enrollment> enrollment = enrollmentRepository.findByUserAndLecture(user, lecture);

        if (enrollment.isEmpty()) {
            throw new BaseException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        // 수강 취소
        enrollmentRepository.delete(enrollment.get());
        lecture.decrementEnrollment();
        lectureRepository.save(lecture);

        return true;
    }
}