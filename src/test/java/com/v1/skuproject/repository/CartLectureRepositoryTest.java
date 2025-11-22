package com.v1.skuproject.repository;

import com.v1.skuproject.domain.cart.Cart;
import com.v1.skuproject.domain.cart.CartLecture;
import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.domain.user.Professor;
import com.v1.skuproject.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class CartLectureRepositoryTest {

    @Autowired
    private CartLectureRepository cartLectureRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LectureRepository lectureRepository;

    // 추후 교수 리포지토리 만들면 수정
    @PersistenceContext
    private EntityManager em;


    private Cart setupCart() {
        User user = User.builder()
                .studentId(2023216049)
                .name("변예섭")
                .password("1234")
                .major(Major.SOFTWARE)
                .build();

        User savedUser = userRepository.save(user);

        Cart cart = Cart.builder()
                .user(savedUser)
                .build();

        return cartRepository.save(cart);
    }

    private Lecture setupLecture(String name) {
        Professor professor = Professor.builder()
                .name("홍길동")
                .build();

        em.persist(professor);

        Lecture lecture = Lecture.builder()
                .lectureName(name)
                .lectureCode("CS101")
                .classNumber(1)
                .type(LectureType.MAJOR_CORE)
                .credit(3)
                .capacity(50)
                .rating(0.0)
                .schedule("월09-10")
                .professor(professor)
                .major(Major.SOFTWARE)
                .build();

        return lectureRepository.save(lecture);
    }

    @Test
    @DisplayName("장바구니에 담긴 강의 전체 조회")
    void findByCart() {
        // given
        Cart cart = setupCart();
        Lecture lecture1 = setupLecture("자료구조");
        Lecture lecture2 = setupLecture("알고리즘");

        cartLectureRepository.save(
                CartLecture.builder().cart(cart).lecture(lecture1).build()
        );

        cartLectureRepository.save(
                CartLecture.builder().cart(cart).lecture(lecture2).build()
        );

        // when
        List<CartLecture> result = cartLectureRepository.findByCart(cart);

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("장바구니 + 특정 강의 조회")
    void findByCartAndLecture() {
        // given
        Cart cart = setupCart();
        Lecture lecture = setupLecture("데이터베이스");

        cartLectureRepository.save(
                CartLecture.builder().cart(cart).lecture(lecture).build()
        );

        // when
        Optional<CartLecture> result =
                cartLectureRepository.findByCartAndLecture(cart, lecture);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getLecture()).isEqualTo(lecture);
    }

    @Test
    @DisplayName("장바구니 + 특정 강의 삭제")
    void deleteByCartAndLecture() {
        // given
        Cart cart = setupCart();
        Lecture lecture = setupLecture("운영체제");

        cartLectureRepository.save(
                CartLecture.builder().cart(cart).lecture(lecture).build()
        );

        // when
        cartLectureRepository.deleteByCartAndLecture(cart, lecture);
        Optional<CartLecture> deleted =
                cartLectureRepository.findByCartAndLecture(cart, lecture);

        // then
        assertThat(deleted).isEmpty();
    }

    @Test
    @DisplayName("장바구니 전체 강의 삭제")
    void deleteAllByCart() {
        // given
        Cart cart = setupCart();
        Lecture lecture1 = setupLecture("소프트웨어공학");
        Lecture lecture2 = setupLecture("네트워크");

        cartLectureRepository.save(
                CartLecture.builder().cart(cart).lecture(lecture1).build()
        );
        cartLectureRepository.save(
                CartLecture.builder().cart(cart).lecture(lecture2).build()
        );

        // when
        cartLectureRepository.deleteAllByCart(cart);
        List<CartLecture> remaining = cartLectureRepository.findByCart(cart);

        // then
        assertThat(remaining).isEmpty();
    }
}