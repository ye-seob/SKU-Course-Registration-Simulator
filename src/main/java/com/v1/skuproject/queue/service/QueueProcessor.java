package com.v1.skuproject.queue.service;

import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
import com.v1.skuproject.enrollment.service.EnrollmentService;
import com.v1.skuproject.queue.model.QueueEntry;
import com.v1.skuproject.queue.notifier.QueueNotifier;
import com.v1.skuproject.queue.repository.QueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueProcessor {

    private final QueueRepository queueRepository;
    private final QueueSubscriberService queueSubscriberService;
    private final EnrollmentService enrollmentService;
    private final QueueNotifier queueNotifier;

    /**
     * 대기열 처리 (수강신청 진행)
     */
    public void processQueue() {

        int processCount = ThreadLocalRandom.current().nextInt(20, 60);

        Set<String> values = queueRepository.range(0, processCount - 1);

        if (values == null || values.isEmpty()) {
            return;
        }

        for (String value : values) {

            if (value == null) {
                continue;
            }

            QueueEntry entry = QueueEntry.decode(value);

            // 더미 유저 처리
            if (entry.isDummy()) {
                processDummy(entry);
            } else {
                processUser(entry);
            }
        }
    }
    private void processDummy(QueueEntry entry){
        Long lectureId = entry.getLectureId();

        enrollmentService.enrollDummy(lectureId);
        exitQueue(entry);
    }

    private void processUser(QueueEntry entry){
        Long userId = entry.getUserId();
        Long lectureId = entry.getLectureId();

        try {
            enrollmentService.enroll(userId, lectureId);

            queueNotifier.sendSuccess(userId, "수강신청이 완료되었습니다.");

            log.info("수강신청 처리 성공 userId={} lectureId={}", userId, lectureId);
        } catch (BaseException e) {

            ErrorCode errorCode = e.getErrorCode();

            queueNotifier.sendFail(userId, errorCode.getMessage());

            log.warn("수강신청 처리 실패 userId={} lectureId={} errorCode={}", userId, lectureId, errorCode.getCode());
        } catch (Exception e) {

            queueNotifier.sendFail(userId, "시스템 오류로 수강신청에 실패했습니다.");

            log.error("수강신청 처리 실패 userId={} lectureId={}", userId, lectureId, e);
        } finally {
            exitQueue(entry);
        }
    }

    private void exitQueue (QueueEntry entry){
        Long userId = entry.getUserId();

        queueRepository.remove(entry.encode());
        queueSubscriberService.unsubscribe(userId);
    }
}