package com.v1.skuproject.dto.user;

import com.v1.skuproject.domain.user.Major;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
        private int studentId;

        @NotBlank(message = "이름을 입력해 주세요.")
        private String name;

        @NotNull(message = "학과를 선택해 주세요.")
        private Major major;

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        @Size(min = 6, max = 20, message = "비밀번호는 6~20자리여야 합니다.")
        private String password;

        @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
        private String passwordConfirm;


        private int grade;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {
        private int studentId;

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        private String password;
    }
}