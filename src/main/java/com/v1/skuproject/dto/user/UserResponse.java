package com.v1.skuproject.dto.user;

import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.domain.user.User;
import lombok.*;

public class UserResponse {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class UserDto {
        private Long id;
        private int studentId;
        private String name;

        private Major major;               // 주전공

        private int grade;                 // 학년
        private int maxCredit;             // 최대 신청 학점

        private String token;

        public static UserDto from(User user, String token) {
            return UserDto.builder()
                    .id(user.getId())
                    .studentId(user.getStudentId())
                    .name(user.getName())
                    .major(user.getMajor())
                    .grade(user.getGrade())
                    .maxCredit(user.getMaxCredit())
                    .token(token)
                    .build();
        }
    }
}