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
            if (!s2.containsKey(day)) continue;

            List<String> times1 = s1.get(day);
            List<String> times2 = s2.get(day);

            List<Integer> t1 = times1.stream().map(Integer::parseInt).toList();
            List<Integer> t2 = times2.stream().map(Integer::parseInt).toList();

            for (int i = 0; i < t1.size(); i += 2) {
                int start1 = t1.get(i);
                int end1 = (i + 1 < t1.size()) ? t1.get(i + 1) : start1;

                for (int j = 0; j < t2.size(); j += 2) {
                    int start2 = t2.get(j);
                    int end2 = (j + 1 < t2.size()) ? t2.get(j + 1) : start2;

                    if (start1 <= end2 && start2 <= end1) {
                        return true;
                    }
                }
            }
        }

        return false; // 충돌 없음
    }
}