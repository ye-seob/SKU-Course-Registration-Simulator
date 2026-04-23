package com.v1.skuproject.practice.service;

import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
import com.v1.skuproject.lecture.entity.Lecture;
import com.v1.skuproject.lecture.repository.LectureRepository;
import com.v1.skuproject.practice.dto.PracticeEnrollmentResponse;
import com.v1.skuproject.practice.entity.PracticeEnrollment;
import com.v1.skuproject.practice.repository.PracticeEnrollmentRepository;
import com.v1.skuproject.user.entity.User;
import com.v1.skuproject.user.repository.UserRepository;
import com.v1.skuproject.util.ScheduleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PracticeService {

    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final PracticeEnrollmentRepository practiceEnrollmentRepository;

    @Value("${enrollment.max-courses}")
    private int maxCourses;

    public List<PracticeEnrollmentResponse> getPracticeEnrollments(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        return practiceEnrollmentRepository.findAllByUser(user)
                .stream()
                .map(PracticeEnrollmentResponse::from)
                .toList();

    }


    /**
     * 연습 수강신청
     */
    @Transactional
    public void practiceEnroll(Long userId, Long lectureId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        // 중복 신청 체크
        if (practiceEnrollmentRepository.existsByUserAndLecture_Id(user, lectureId)) {
            throw new BaseException(ErrorCode.ENROLLMENT_DUPLICATE);
        }

        // 최대 과목 수 체크
        List<PracticeEnrollment> enrolled = practiceEnrollmentRepository.findAllByUser(user);
        if (enrolled.size() >= maxCourses) {
            throw new BaseException(ErrorCode.ENROLLMENT_MAX_LIMIT_EXCEEDED);
        }

        // 시간표 겹침 체크
        for (PracticeEnrollment e : enrolled) {
            if (ScheduleUtil.hasConflict(lecture.getSchedule(), e.getLecture().getSchedule())) {
                throw new BaseException(ErrorCode.ENROLLMENT_TIME_CONFLICT);
            }
        }

        practiceEnrollmentRepository.save(PracticeEnrollment.of(user, lecture));

        log.info("연습 수강신청 성공 userId={} lectureId={}", userId, lectureId);
    }

    /**
     * 연습 수강신청 취소
     */
    @Transactional
    public void practiceCancel(Long userId, Long lectureId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        PracticeEnrollment enrollment = practiceEnrollmentRepository
                .findByUserAndLecture_Id(user, lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.ENROLLMENT_NOT_FOUND));


        practiceEnrollmentRepository.delete(enrollment);

        log.info("연습 수강신청 취소 userId={} lectureId={}", userId, lectureId);
    }
}