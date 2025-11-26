package com.v1.skuproject.repository;

import com.v1.skuproject.domain.lecture.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long>, JpaSpecificationExecutor {
}