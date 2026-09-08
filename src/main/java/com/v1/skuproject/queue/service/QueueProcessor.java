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

        Set<String> values = queueRepository.range(0, -1);

        if (values == null || values.isEmpty()) {
            return;
        }

        for (String value : values){
            if (value == null) {
                continue;
            }

            QueueEntry entry = QueueEntry.decode(value);

            processUser(entry);

        }
    }
    

    private void processUser(QueueEntry entry){
        Long userId = entry.getUserId();
        Long lectureId = entry.getLectureId();

        try {
            enrollmentService.enroll(userId, lectureId);

            queueNotifier.sendSuccess(userId, "수강신청이 완료되었습니다.");

        } catch (BaseException e) {

            ErrorCode errorCode = e.getErrorCode();

            queueNotifier.sendFail(userId, errorCode.getMessage());

        } catch (Exception e) {

            queueNotifier.sendFail(userId, "시스템 오류로 수강신청에 실패했습니다.");

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