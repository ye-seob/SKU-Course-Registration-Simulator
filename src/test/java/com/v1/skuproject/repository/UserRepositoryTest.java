package com.v1.skuproject.repository;

import com.v1.skuproject.domain.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test

    void findByStudentId() {
        // given
        User user = User.builder()
                .studentId(2023216049)
                .name("변예섭")
                .password("1234")
                .build();

        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByStudentId(2023216049);

        // then
        assertTrue(result.isPresent());
        assertEquals("변예섭", result.get().getName());
    }
}