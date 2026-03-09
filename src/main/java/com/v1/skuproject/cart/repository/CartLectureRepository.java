package com.v1.skuproject.cart.repository;

import com.v1.skuproject.cart.entity.Cart;
import com.v1.skuproject.cart.entity.CartLecture;
import com.v1.skuproject.cart.entity.CartLectureId;
import com.v1.skuproject.lecture.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartLectureRepository extends JpaRepository<CartLecture, CartLectureId> {
    // 장바구니에 담긴 강의 전체 조회
    List<CartLecture> findByCart(Cart cart);

    // 장바구니 + 강의로 장바구니에 담긴 강의 조회 (존재 여부 확인용)
    Optional<CartLecture> findByCartAndLecture(Cart cart, Lecture lecture);

    // 장바구니에 담긴 강의 삭제
    void deleteByCartAndLecture(Cart cart, Lecture lecture);

    // 장바구니에 담긴 강의 수 조회
    int countByCart(Cart cart);
}