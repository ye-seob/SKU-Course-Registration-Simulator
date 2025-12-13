package com.v1.skuproject.service;

import com.v1.skuproject.domain.enrollment.Enrollment;
import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.user.User;
import com.v1.skuproject.dto.enrollment.EnrollmentResponse;
import com.v1.skuproject.repository.EnrollmentRepository;
import com.v1.skuproject.repository.LectureRepository;
import com.v1.skuproject.repository.UserRepository;
import com.v1.skuproject.util.exception.BaseException;
import com.v1.skuproject.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Slf4j
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;

    @Transactional
    public EnrollmentResponse enroll(Long userId, Long lectureId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        // 중복 신청
        if (enrollmentRepository.findByUserAndLecture(user, lecture).isPresent()) {

            Enrollment fail = enrollmentRepository.save(
                    Enrollment.fail(user, lecture, "DUPLICATE")
            );

            log.info("수강신청 실패(DUPLICATE) userId={}, lectureId={}", userId, lectureId);
            return EnrollmentResponse.from(fail);
        }

        // 정원 초과
        if (lecture.getEnrollment() >= lecture.getCapacity()) {
            Enrollment fail = enrollmentRepository.save(
                    Enrollment.fail(user, lecture, "CAPACITY_EXCEEDED")
            );
            log.info("수강신청 실패(CAPACITY_EXCEEDED) userId={}, lectureId={}", userId, lectureId);
            return EnrollmentResponse.from(fail);
        }

        // 성공
        Enrollment success = enrollmentRepository.save(
                Enrollment.success(user, lecture)
        );

        lecture.incrementEnrollment();

        lectureRepository.save(lecture);

        log.info("수강신청 성공 userId={}, lectureId={}, enrollment={}/{}",
                userId,
                lectureId,
                lecture.getEnrollment(),
                lecture.getCapacity()
        );

        return EnrollmentResponse.from(success);
    }
}