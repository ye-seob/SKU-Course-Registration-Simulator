package com.v1.skuproject.util;

import com.v1.skuproject.common.exception.BaseException;
import com.v1.skuproject.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Slf4j
@Component
public class TimeChecker {

    @Value("${lecture.operation.start-hour}")
    private int startHour;

    @Value("${lecture.operation.end-hour}")
    private int endHour;

    @Value("${lecture.operation.open-minute}")
    private int openMinute;

    @Value("${lecture.operation.close-minute}")
    private int closeMinute;

    public void validate() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();

        if (hour < startHour || hour >= endHour) {
            log.warn(
                    "[시간 차단] 현재 시각: {}, 허용 시간: {}시 ~ {}시",
                    now, startHour, endHour
            );
            throw new BaseException(ErrorCode.ENROLLMENT_TIME_INVALID);
        }

        if (minute < openMinute || minute >= closeMinute) {
            log.warn(
                    "[분 단위 차단] 현재 시각: {}, 허용 분: {}분 ~ {}분",
                    now, openMinute, closeMinute
            );
            throw new BaseException(ErrorCode.ENROLLMENT_TIME_INVALID);
        }
    }
}