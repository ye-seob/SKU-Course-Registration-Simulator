package com.v1.skuproject.repository;

import com.v1.skuproject.domain.lecture.Lecture;
import com.v1.skuproject.domain.lecture.LectureType;
import com.v1.skuproject.domain.user.Major;
import com.v1.skuproject.domain.user.Professor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class LectureRepositoryTest {

    @Autowired
    private LectureRepository lectureRepository;

    // 추후 교수 리포지토리 만들면 수정
    @PersistenceContext
    private EntityManager em;

    private Professor prof1;
    private Professor prof2;

    private Lecture lecture1;
    private Lecture lecture2;
    private Lecture lecture3;

    @BeforeEach
    void setUp() {
        // given
        // 교수 엔티티 생성 , 추후 교수 리포지토리 만들면 수정
        prof1 = Professor.builder().name("홍길동").build();
        prof2 = Professor.builder().name("김철수").build();

        em.persist(prof1);
        em.persist(prof2);

        lecture1 = Lecture.builder()
                .lectureName("컴퓨터구조")
                .lectureCode("CS101")
                .classNumber(1)
                .major(Major.ELECTRONICS_COMPUTER)
                .type(LectureType.MAJOR_CORE)
                .credit(3)
                .professor(prof1)
                .capacity(30)
                .rating(5.0)
                .schedule("mon 09-12")
                .build();

        lecture2 = Lecture.builder()
                .lectureName("자료구조")
                .lectureCode("CS102")
                .classNumber(1)
                .major(Major.SOFTWARE)
                .type(LectureType.MAJOR_CORE)
                .credit(3)
                .professor(prof2)
                .capacity(30)
                .rating(5.0)
                .schedule("mon 09-12")
                .build();

        lecture3 = Lecture.builder()
                .lectureName("철학입문")
                .lectureCode("GEN101")
                .classNumber(1)
                .major(null)
                .type(LectureType.GENERAL_ELECTIVE)
                .credit(2)
                .professor(prof1)
                .capacity(30)
                .rating(5.0)
                .schedule("mon 09-12")
                .build();

        lectureRepository.save(lecture1);
        lectureRepository.save(lecture2);
        lectureRepository.save(lecture3);
    }

    @Test
    @DisplayName("전공으로 강의 조회")
    void findByMajor() {
        // when
        List<Lecture> result = lectureRepository.findByMajor(Major.SOFTWARE);

        // then
        assertEquals(1, result.size());
        assertEquals("자료구조", result.get(0).getLectureName());
    }

    @Test
    @DisplayName("타입으로 강의 조회")
    void findByType() {
        // when
        List<Lecture> result = lectureRepository.findByType(LectureType.GENERAL_ELECTIVE);

        // then
        assertEquals(1, result.size());
        assertEquals("철학입문", result.get(0).getLectureName());
    }

    @Test
    @DisplayName("전공 + 유형 조회")
    void findByMajorAndType() {
        // when
        List<Lecture> result = lectureRepository.findByMajorAndType(
                Major.ELECTRONICS_COMPUTER,
                LectureType.MAJOR_CORE
        );

        // then
        assertEquals(1, result.size());
        assertEquals("컴퓨터구조", result.get(0).getLectureName());
    }

    @Test
    @DisplayName("강의명 포함 검색")
    void findByLectureNameContaining() {
        // when
        List<Lecture> result = lectureRepository.findByLectureNameContaining("자료");

        // then
        assertEquals(1, result.size());
        assertEquals("자료구조", result.get(0).getLectureName());
    }
}