package com.v1.skuproject.service;

import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.domain.user.User;
import com.v1.skuproject.dto.user.UserRequest;
import com.v1.skuproject.dto.user.UserResponse.UserDto;
import com.v1.skuproject.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserRequest.SignUp signUpRequest;
    @BeforeEach
    void setUp() {
        signUpRequest = UserRequest.SignUp.builder()
                .studentId(2023216049)
                .name("변예섭")
                .major(Major.SOFTWARE)
                .password("q1w2e3r4!")
                .build();
    }

    @Test
    void createUser() {
        // when
        Long userId = userService.createUser(signUpRequest);

        // then
        User saved = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 저장되지 않았습니다."));

        assertThat(saved.getStudentId()).isEqualTo(2023216049);
        assertThat(saved.getName()).isEqualTo("변예섭");
        assertThat(saved.getMajor()).isEqualTo(Major.SOFTWARE);
    }

    @Test
    void login() {
        // given
        Long userId = userService.createUser(signUpRequest);

        UserRequest.Login loginRequest = UserRequest.Login.builder()
                .studentId(2023216049)
                .password("q1w2e3r4!")
                .build();

        // when
        UserDto loginUserId = userService.login(loginRequest);

        // then
        assertThat(loginUserId).isEqualTo(userId);
    }

    @Test
    void getUserById() {
        // given
        Long userId = userService.createUser(signUpRequest);

        // when
        UserDto user = userService.getUserById(userId);

        // then
        assertThat(user.getStudentId()).isEqualTo(2023216049);
        assertThat(user.getName()).isEqualTo("변예섭");
    }

    @Test
    void deleteUserById() {
        // given
        Long userId = userService.createUser(signUpRequest);

        // when
        userService.deleteUserById(userId);

        // then
        boolean exists = userRepository.existsById(userId);
        assertThat(exists).isFalse();
    }
}