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
        private Major major;
        private String token;

        public static UserDto from(User user, String token) {
            return UserDto.builder()
                    .id(user.getId())
                    .studentId(user.getStudentId())
                    .name(user.getName())
                    .major(user.getMajor())
                    .token(token)
                    .build();
        }
    }
}