package com.v1.skuproject.repository;

import com.v1.skuproject.domain.cart.Cart;
import com.v1.skuproject.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    // 유저의 장바구니 조회
    Optional<Cart> findByUser(User user);

    // 유저의 장바구니 존재 여부
    boolean existsByUser(User user);

    // 유저 기준 장바구니 삭제(회원 탈퇴 등등)
    void deleteByUser(User user);
}