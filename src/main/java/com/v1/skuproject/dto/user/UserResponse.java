package com.v1.skuproject.dto.user;

import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserDto {
        private Long id;
        private int studentId;
        private String name;
        private Major major;

        public static UserDto from(User user) {
            return UserDto.builder()
                    .id(user.getId())
                    .studentId(user.getStudentId())
                    .name(user.getName())
                    .major(user.getMajor())
                    .build();
        }
    }
}