package com.seniorjob.seniorjobserver.repository;

import com.seniorjob.seniorjobserver.domain.entity.LectureProposalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureProposalRepository extends JpaRepository<LectureProposalEntity, Long>{

}
