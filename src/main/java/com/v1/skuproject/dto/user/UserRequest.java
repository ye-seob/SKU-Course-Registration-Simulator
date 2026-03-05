package com.v1.skuproject.dto.user;

import com.v1.skuproject.domain.user.Major;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUp {
        private String studentId;

        @NotBlank(message = "이름을 입력해 주세요.")
        private String name;

        @NotNull(message = "학과를 선택해 주세요.")
        private Major major;

        private int grade;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        private String studentId;
        private String loginMode;
    }

    @Getter
    @NoArgsConstructor
    public  static  class GuestLoginRequest {
        private Major major;
    }
}