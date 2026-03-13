package com.v1.skuproject.queue.service;

import com.v1.skuproject.queue.dto.QueueRankResponse;
import com.v1.skuproject.queue.model.QueueEntry;
import com.v1.skuproject.queue.notifier.QueueNotifier;
import com.v1.skuproject.queue.repository.QueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueueService {

    private final QueueSubscriberService queueSubscriberService;
    private final QueueRepository queueRepository;
    private final QueueNotifier queueNotifier;


    /**
     * 대기열 등록
     */
    public void enter(Long userId, Long lectureId) {

        String value = QueueEntry.of(userId, lectureId).encode();

        if(queueRepository.rank(value) != null){
            return;
        }

        queueRepository.add(value, System.currentTimeMillis());

        if(userId >= 100_000L){
            return;
        }

        // 실제 유저만 ws 등록
        queueSubscriberService.subscribe(userId, lectureId);
        queueNotifier.sendRank(userId, getRank(userId, lectureId));
    }


    /**
     * 대기열 이탈
     */
    public void exit(Long userId, Long lectureId) {

        String value = QueueEntry.of(userId, lectureId).encode();

        queueRepository.remove(value);

        queueSubscriberService.unsubscribe(userId);
    }

    /**
     * 현재 사용자의 대기열 순번 조회
     */
    public QueueRankResponse getRank(Long userId, Long lectureId) {

        String value = QueueEntry.of(userId, lectureId).encode();

        Long rank = queueRepository.rank(value);
        Long totalSize = queueRepository.size();


        if (rank == null || totalSize == null) {
            throw new IllegalStateException("대기열 정보가 존재하지 않음");
        }


        return QueueRankResponse.builder()
                .aheadCount(rank)
                .behindCount(totalSize - rank - 1)
                .build();
    }

    /**
     * 모든 구독자에게 현재 대기열 순번 Push
     */
    public void pushQueueUpdate() {
        queueSubscriberService.getSubscribers().forEach((userId,lectureId)->{
            try{
                queueNotifier.sendRank(userId,getRank(userId,lectureId));
            }catch (Exception e){
                log.error("대기열 순번 전송 실패 userId={} , lectureId={}",userId,lectureId,e);
            }
        });
    }

    /**
     * 대기열 전체 초기화
     */
    public void clearAllQueues() {

        log.info("대기열 전체 초기화 시작");

        queueRepository.clear();
        queueSubscriberService.clear();

        log.info("대기열 전체 초기화 완료");
    }


}