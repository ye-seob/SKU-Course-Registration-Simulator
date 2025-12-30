package com.v1.skuproject.service;

import com.v1.skuproject.dto.queue.QueueRankResponse;
import com.v1.skuproject.util.exception.BaseException;
import com.v1.skuproject.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueueService {

    // redis Queue의 Key
    private static final String QUEUE_KEY = "enrollment:queue";
    private final StringRedisTemplate redisTemplate;

    /** 대기열 등록 **/
    public void enter(Long userId, Long lectureId) {
        String value = generateValue(userId, lectureId);

        long now = System.currentTimeMillis();

        // 신청 버튼 누르자마자 내 앞에 100~300명 랜덤 생성
        int dummyNum = (int) (Math.random() * 201) + 100;

        for (int i = 0; i < dummyNum; i++) {
            String dummyValue = "dummy" + ":" + (i+"dummy");
            redisTemplate.opsForZSet().add(QUEUE_KEY, dummyValue, (double) (now - (i * 100L)));
        }

        // 사용자 신청 Set에 추가
        redisTemplate.opsForZSet().add(QUEUE_KEY, value, (double) now);
    }

    /** 현재 대기열 상태 조회 (앞에 남은 인원, 뒤에 있는 인원) **/
    public QueueRankResponse getRank(Long userId, Long lectureId) {
        String value = generateValue(userId, lectureId);

        // 조회할 때마다 내 뒤로 50~80명 유입
        int dummyNum = (int) (Math.random() * 51) + 30;

        long now = System.currentTimeMillis();

        for (int i = 0; i < dummyNum; i++) {
            String dummyValue = "dummy" + ":" + ("dummy" + i);
            redisTemplate.opsForZSet().add(QUEUE_KEY, dummyValue, (double) now + (i * 10L));
        }

        // 나의 순위 조회
        Long rank = redisTemplate.opsForZSet().rank(QUEUE_KEY, value);

        // 전체 인원수 조회
        Long totalSize = redisTemplate.opsForZSet().zCard(QUEUE_KEY);

        if (rank == null || totalSize == null) {
	     throw new BaseException(ErrorCode.INTERNAL_ERROR);
        }

        // 내 앞 인원
        Long aheadCount = rank;

        // 내 뒤 인원 = 전체 인원 - 내 순위 - 1
        Long behindCount = totalSize - rank - 1;

        return QueueRankResponse.builder()
                .aheadCount(aheadCount)
                .behindCount(behindCount)
                .build();
    }


    /** 대기열 취소 (새로고침 / 창 닫기) */
    public void exit(Long userId, Long lectureId) {
        String value = generateValue(userId, lectureId);

        redisTemplate.opsForZSet().remove(QUEUE_KEY, value);
    }

    /** 키 생성 메소드 **/
    private String generateValue(Long userId, Long lectureId) {
        return userId + ":" + lectureId;
    }
}
