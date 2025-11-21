package com.v1.skuproject.repository;

import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("학번으로 유저 조회")
    void findByStudentId() {
        // given
        User user = User.builder()
                .studentId(2023216049)
                .name("변예섭")
                .password("1234")
                .major(Major.SOFTWARE)
                .build();

        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByStudentId(2023216049);

        // then
        assertTrue(result.isPresent(), "유저가 존재해야 합니다.");
        assertEquals("변예섭", result.get().getName());
        assertEquals(2023216049, result.get().getStudentId());
    }

    @Test
    @DisplayName("학번,비밀번호로 유저 조회")
    void findByStudentIdAndPassword() {
        // given
        User user = User.builder()
                .studentId(2023216049)
                .name("변예섭")
                .password("1234")
                .major(Major.SOFTWARE)
                .build();

        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByStudentIdAndPassword(2023216049, "1234");

        // then
        assertTrue(result.isPresent(), "유저가 존재해야 합니다.");
        assertEquals("변예섭", result.get().getName());
    }

    @Test
    @DisplayName("학번으로 존재 여부 확인")
    void existsByStudentId() {
        // given
        User user = User.builder()
                .studentId(2023216049)
                .name("변예섭")
                .password("1234")
                .major(Major.SOFTWARE)
                .build();

        userRepository.save(user);

        // when
        Boolean exists = userRepository.existsByStudentId(2023216049);

        // then
        assertTrue(exists, "유저가 존재해야 합니다.");
    }

    @Test
    @DisplayName("학번으로 유저 삭제")
    void deleteByStudentId() {
        // given
        User user = User.builder()
                .studentId(2023216049)
                .name("변예섭")
                .password("1234")
                .major(Major.SOFTWARE)
                .build();

        userRepository.save(user);

        // when
        userRepository.deleteByStudentId(2023216049);

        // then
        assertFalse(userRepository.findByStudentId(2023216049).isPresent(), "유저가 삭제되어야 합니다.");
    }
}