package com.v1.skuproject.cart.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CartLectureId implements Serializable {

    @Column(name = "cart_id")
    private Long cartId;

    @Column(name = "lecture_id")
    private Long lectureId;
}