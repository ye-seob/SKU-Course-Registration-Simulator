package com.v1.skuproject.repository;

import com.v1.skuproject.domain.cart.Cart;
import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;



@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("유저 장바구니 조회")
    void findByUser() {
        // given
        User user = User.builder()
                .studentId(2023216049)
                .name("변예섭")
                .password("1234")
                .major(Major.SOFTWARE)
                .build();

        User savedUser = userRepository.save(user);

        Cart cart = Cart.builder().user(savedUser).build();

        cartRepository.save(cart);

        // when
        Optional<Cart> result = cartRepository.findByUser(savedUser);



        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("장바구니 존재 여부 확인")
    void existsByUser() {
        // given
        User user = User.builder()
                .studentId(2023216049)
                .name("변예섭")
                .password("1234")
                .major(Major.SOFTWARE)
                .build();

        User savedUser = userRepository.save(user);

        Cart cart = Cart.builder().user(savedUser).build();

        cartRepository.save(cart);

        // when
        boolean exists = cartRepository.existsByUser(savedUser);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("장바구니 삭제")
    void deleteByUser() {
        // given
        User user = User.builder()
                .studentId(2023216049)
                .name("변예섭")
                .password("1234")
                .major(Major.SOFTWARE)
                .build();

        User savedUser = userRepository.save(user);

        Cart cart = Cart.builder().user(savedUser).build();

        cartRepository.save(cart);

        // when
        cartRepository.deleteByUser(user);
        Optional<Cart> deleted = cartRepository.findByUser(savedUser);

        // then
        assertThat(deleted).isEmpty();
    }
}