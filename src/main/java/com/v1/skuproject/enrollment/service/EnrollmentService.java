package com.v1.skuproject.enrollment.service;

import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
import com.v1.skuproject.enrollment.dto.EnrollmentResponse;
import com.v1.skuproject.enrollment.entity.Enrollment;
import com.v1.skuproject.enrollment.repository.EnrollmentRepository;
import com.v1.skuproject.lecture.entity.Lecture;
import com.v1.skuproject.lecture.repository.LectureRepository;
import com.v1.skuproject.user.entity.User;
import com.v1.skuproject.user.repository.UserRepository;
import com.v1.skuproject.util.ScheduleUtil;
import com.v1.skuproject.util.TimeChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final TimeChecker timeChecker;
    @Value("${enrollment.max-courses}")
    private int maxCourses;

    /**
     * 사용자의 수강 신청 목록 조회
     */
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollments(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        return enrollmentRepository.findAllByUser(user)
                .stream()
                .map(EnrollmentResponse::from)
                .toList();
    }

    /**
     * 수강 신청
     */
    @Transactional
    public EnrollmentResponse enroll(Long userId, Long lectureId) {

        timeChecker.validateEnrollment();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        // 중복 신청 체크
        if (enrollmentRepository.findByUser_IdAndLecture_Id(userId, lectureId).isPresent()) {
            log.info("수강신청 실패(DUPLICATE) userId={} lectureId={}", userId, lectureId);
            throw new BaseException(ErrorCode.ENROLLMENT_DUPLICATE);
        }

        // 이미 신청한 강의 목록
        List<Enrollment> userEnrollments =
                enrollmentRepository.findAllByUser(user);

        if (userEnrollments.size() >= maxCourses) {
            log.info("수강신청 실패(MAX_LIMIT) userId={} lectureId={}", userId, lectureId);
            throw new BaseException(ErrorCode.ENROLLMENT_MAX_LIMIT_EXCEEDED);
        }


        // 시간표 겹침 체크
        for (Enrollment e : userEnrollments) {
            if (ScheduleUtil.hasConflict(
                    lecture.getSchedule(),
                    e.getLecture().getSchedule())
            ) {
                log.info("수강신청 실패(TIME_CONFLICT) userId={} lectureId={}", userId, lectureId);
                throw new BaseException(ErrorCode.ENROLLMENT_TIME_CONFLICT);
            }
        }

        lecture.enroll();

        // 수강신청 성공 처리
        Enrollment enrollment = enrollmentRepository.save(
                Enrollment.success(user, lecture)
        );

        log.info(
                "수강신청 성공 userId={} lectureId={} enrollment={}/{}",
                userId,
                lectureId,
                lecture.getEnrollment(),
                lecture.getCapacity()
        );

        return EnrollmentResponse.from(enrollment);
    }

    /**
     * 더미 유저 수강신청 처리 (큐용)
     */
    @Transactional
    public void enrollDummy(Long lectureId) {

        timeChecker.validateEnrollment();

        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        lecture.enroll();
    }

    /**
     * 수강 신청 취소
     */
    @Transactional
    public EnrollmentResponse cancelEnrollment(Long userId, Long lectureId) {

         userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        Enrollment enrollment = enrollmentRepository
                .findByUser_IdAndLecture_Id(userId, lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.ENROLLMENT_NOT_FOUND));

        lecture.cancel();

        enrollmentRepository.delete(enrollment);


        log.info("수강신청 취소 userId={} lectureId={}", userId, lectureId);

        return EnrollmentResponse.from(enrollment);
    }

    /**
     * 50분 - 신청 내역 초기화
     */
    @Transactional
    public void resetEnrollment() {
        enrollmentRepository.deleteAllInBatch();
        log.info("신청 내역 초기화 완료");
    }

    /**
     * 50분 - 강의별 신청 인원 초기화
     */
    @Transactional
    public void resetLectureCounts() {
        lectureRepository.findAll().forEach(Lecture::resetEnrolledCount);
        log.info("강의 신청 인원 초기화 완료");
    }
    @Transactional
    public void deleteGuestEnrollment() {

        LocalDateTime time = LocalDateTime.now().minusHours(1);

        int deleted =  enrollmentRepository.deleteGuestEnrollments(time);

        log.info("삭제된 비회원 유저의 수강 신청 내역 = {}",deleted);
    }
}