package com.v1.skuproject.util;

import com.v1.skuproject.util.exception.BaseException;
import com.v1.skuproject.util.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

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
            throw new BaseException(ErrorCode.INTERNAL_ERROR);
        }

        if (minute < openMinute || minute >= closeMinute) {
            throw new BaseException(ErrorCode.INTERNAL_ERROR);
        }
    }
}