package com.v1.skuproject.service;

import com.v1.skuproject.domain.enrollment.Enrollment;
import com.v1.skuproject.domain.enrollment.EnrollmentStatus;
import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.user.User;
import com.v1.skuproject.dto.enrollment.EnrollmentResponse;
import com.v1.skuproject.repository.EnrollmentRepository;
import com.v1.skuproject.repository.LectureRepository;
import com.v1.skuproject.repository.UserRepository;
import com.v1.skuproject.util.ScheduleUtil;
import com.v1.skuproject.util.TimeChecker;
import com.v1.skuproject.util.exception.BaseException;
import com.v1.skuproject.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private final TimeChecker timeChecker;

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollments(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        return enrollmentRepository.findAllByUserAndStatus(user, EnrollmentStatus.SUCCESS)
                .stream()
                .map(EnrollmentResponse::from)
                .toList();
    }

    @Transactional
    public EnrollmentResponse enroll(Long userId, Long lectureId) {

        timeChecker.validate();


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        // 중복 신청
        if (enrollmentRepository.findByUser_IdAndLecture_Id(userId, lectureId).isPresent()) {
            log.info("수강신청 실패(DUPLICATE) userId={}, lectureId={}", userId, lectureId);
            return EnrollmentResponse.fail("중복 신청");
        }

        // 정원 초과
        if (lecture.getEnrollment() >= lecture.getCapacity()) {
            log.info("수강신청 실패(CAPACITY_EXCEEDED) userId={}, lectureId={}", userId, lectureId);
            return EnrollmentResponse.fail("정원 초과");
        }

        // 이미 신청한 강의 목록 조회
        List<Enrollment> userEnrollments = enrollmentRepository.findAllByUserAndStatus(user, EnrollmentStatus.SUCCESS);

        // 최대 수강 제한 체크
        int MAX_COURSES = 10;
        if (userEnrollments.size() >= MAX_COURSES) {
            log.info("수강신청 실패(MAX_LIMIT) userId={}, lectureId={}", userId, lectureId);
            return EnrollmentResponse.fail("최대 10개의 강의까지 신청 가능합니다");
        }

        for (Enrollment e : userEnrollments) {
            if (ScheduleUtil.hasConflict(lecture.getSchedule(), e.getLecture().getSchedule())) {
                log.info("수강신청 실패(TIME_CONFLICT) userId={}, lectureId={}", userId, lectureId);
                return EnrollmentResponse.fail("시간 겹침");
            }
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

    @Transactional
    public void enrollDummy(Long lectureId) {

        timeChecker.validate();

        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));

        if (lecture.getEnrollment() >= lecture.getCapacity()) {
            return;
        }

        lecture.incrementEnrollment();
        lectureRepository.save(lecture);

    }

    @Transactional
    public EnrollmentResponse cancelEnrollment(Long userId, Long lectureId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.LECTURE_NOT_FOUND));


        Enrollment enrollment = enrollmentRepository.findByUser_IdAndLecture_Id(userId,lectureId)
                .orElseThrow(() -> new BaseException(ErrorCode.ENROLLMENT_NOT_FOUND));



        enrollmentRepository.delete(enrollment);

        lecture.decrementEnrollment();
        lectureRepository.save(lecture);

        log.info("수강신청 취소 userId={}, lectureId={}", userId, lectureId);
        return EnrollmentResponse.from(enrollment);
    }



    // 55분 - 신청 내역 초기화
    @Transactional
    public void resetEnrollmentStatuses() {
        log.info("신청 내역 초기화 시작");
        enrollmentRepository.deleteAllInBatch(); // 기존 신청 내역 삭제
        log.info("신청 내역 초기화 완료");
    }

    // 55분 - 강의별 신청 인원 초기화
    @Transactional
    public void resetLectureCounts() {
        log.info("강의 신청 인원 초기화 시작");
        lectureRepository.findAll().forEach(Lecture::resetEnrolledCount);
        log.info("강의 신청 인원 초기화 완료");
    }

}