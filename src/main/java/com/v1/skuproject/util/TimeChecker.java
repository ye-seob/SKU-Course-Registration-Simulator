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

    public void validateEnrollment() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();

        if (hour < startHour || hour > endHour) {
            log.warn(
                    "[시간 차단] 현재 시각: {}, 허용 시간: {}시 ~ {}시",
                    now, startHour, endHour
            );
            throw new BaseException(ErrorCode.ENROLLMENT_TIME_INVALID);
        }

        if (minute >= 50) {
            log.warn(
                    "[수강신청 차단] 현재 시각: {}",
                    now
            );
            throw new BaseException(ErrorCode.ENROLLMENT_TIME_INVALID);
        }

    }

    public void validateLogin() {
        LocalTime now = LocalTime.now();
        int minute = now.getMinute();

        // 50분 ~ 59분 59초 59 로그인 차단
        if (minute >= 50) {
            log.warn(
                    "[로그인 차단] 현재 시각: {}",
                    now
            );
            throw new BaseException(ErrorCode.ENROLLMENT_TIME_INVALID);
        }

    }
}