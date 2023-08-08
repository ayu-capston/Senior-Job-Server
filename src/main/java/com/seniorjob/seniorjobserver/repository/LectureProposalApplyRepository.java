package com.seniorjob.seniorjobserver.repository;

import com.seniorjob.seniorjobserver.domain.entity.*;
import com.seniorjob.seniorjobserver.dto.LectureProposalApplyDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureProposalApplyRepository extends JpaRepository<LectureProposalApplyEntity, Long> {
    boolean existsByUserAndLectureProposal(UserEntity user, LectureProposalEntity lectureProposal);

    Optional<LectureProposalApplyEntity> findByUserAndLectureProposal(UserEntity user, LectureProposalEntity lectureProposal);

    List<LectureProposalApplyEntity> findByLectureProposal(LectureProposalEntity lectureProposal);

    List<LectureProposalApplyEntity> findByLectureProposalAndLectureProposalApplyStatus(LectureProposalEntity lectureProposal, LectureProposalApplyEntity.LectureProposalApplyStatus status);

    Optional<Object> findByLectureProposalAndRecruitmentClosed(LectureProposalEntity lectureProposal, boolean b);

}
