package com.v1.skuproject.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class ScheduleUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, List<String>> parse(String json) {
        try {
            return mapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("스케줄 파싱 실패", e);
        }
    }

    public static boolean hasConflict(String schedule1Json, String schedule2Json) {
        Map<String, List<String>> s1 = parse(schedule1Json);
        Map<String, List<String>> s2 = parse(schedule2Json);

        for (String day : s1.keySet()) {
            if (s2.containsKey(day)) {
                List<String> range1 = s1.get(day);
                List<String> range2 = s2.get(day);

                int start1 = Integer.parseInt(range1.get(0));
                int end1 = Integer.parseInt(range1.get(1));

                int start2 = Integer.parseInt(range2.get(0));
                int end2 = Integer.parseInt(range2.get(1));

                // 범위 겹침 체크
                if (start1 <= end2 && start2 <= end1) {
                    return true; // 겹침 o
                }
            }
        }
        return false; // 겹침 x
    }
}