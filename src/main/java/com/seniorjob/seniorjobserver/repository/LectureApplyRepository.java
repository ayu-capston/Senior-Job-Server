package com.seniorjob.seniorjobserver.repository;

import com.seniorjob.seniorjobserver.domain.entity.LectureApplyEntity;
import com.seniorjob.seniorjobserver.domain.entity.LectureEntity;
import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LectureApplyRepository extends JpaRepository<LectureApplyEntity, Long> {

    boolean existsByUserAndLecture(UserEntity user, LectureEntity lecture);

    Optional<LectureApplyEntity> findByUserAndLecture(UserEntity user, LectureEntity lecture);

    List<LectureApplyEntity> findByUser(UserEntity user);
}

