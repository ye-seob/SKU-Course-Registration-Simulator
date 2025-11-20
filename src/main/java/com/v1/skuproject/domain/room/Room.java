package com.v1.skuproject.domain.room;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // room 만료 시간
    @Column(name = "ttl_expire_at", nullable = false)
    private LocalDateTime ttlExpireAt;

    // 난이도
    @Column(nullable = false)
    private int difficulty;

    // 방 생성 시간
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Room에 담긴 강의들
    @OneToMany(mappedBy = "room")
    private List<RoomLecture> roomLectures = new ArrayList<>();
}