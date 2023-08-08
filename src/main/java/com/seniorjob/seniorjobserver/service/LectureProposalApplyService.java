package com.seniorjob.seniorjobserver.service;

import com.seniorjob.seniorjobserver.domain.entity.*;
import com.seniorjob.seniorjobserver.repository.LectureProposalApplyRepository;
import com.seniorjob.seniorjobserver.repository.LectureProposalRepository;
import com.seniorjob.seniorjobserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LectureProposalApplyService {

    private final LectureProposalRepository lectureProposalRepository;
    private final LectureProposalApplyRepository lectureProposalApplyRepository;
    private final UserRepository userRepository;

    @Autowired
    public LectureProposalApplyService(UserRepository userRepository, LectureProposalRepository lectureProposalRepository, LectureProposalApplyRepository lectureProposalApplyRepository){
        this.userRepository = userRepository;
        this.lectureProposalRepository = lectureProposalRepository;
        this.lectureProposalApplyRepository = lectureProposalApplyRepository;
    }

    // 강좌제안 참여신청
    public void applyForLectureProposal(Long userId, Long proposal_id, String applyReason){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. id: " + userId));

        LectureProposalEntity lectureProposal = lectureProposalRepository.findById(proposal_id)
                .orElseThrow(() -> new RuntimeException("강좌제안를 찾을 수 없습니다. id: " + proposal_id));

        // 이미 강좌제안에 참여한 경우 예외 처리
        if (lectureProposalApplyRepository.existsByUserAndLectureProposal(user, lectureProposal)) {
            throw new RuntimeException(lectureProposal + " 이미 참여하신 강좌입니다.");
        }

        // 모집마감된 경우 예외 처리
        if (lectureProposalApplyRepository.findByLectureProposalAndRecruitmentClosed(lectureProposal, true).isPresent()) {
            throw new RuntimeException("모집이 마감된 강좌제안에는 신청할 수 없습니다.");
        }

        // 강좌제안 참여 생성
        LectureProposalApplyEntity lectureProposalApply = LectureProposalApplyEntity.builder()
                .lectureProposal(lectureProposal)
                .user(user)
                .createdDate(LocalDateTime.now())
                .applyReason(applyReason)
                .build();
        lectureProposal.increaseCurrentParticipants();
        lectureProposalApply.setLectureProposalApplyStatus(LectureProposalApplyEntity.LectureProposalApplyStatus.대기);
        lectureProposalApplyRepository.save(lectureProposalApply);
    }

    // 강좌제안신청 취소
    public String cancleLectureProposalApply(Long userId, Long proposalId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. userid : " + userId));

        LectureProposalEntity lectureProposal = lectureProposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("강좌제안를 찾을 수 없습니다. proposalid : " + proposalId));

        LectureProposalApplyEntity lectureProposalApply = lectureProposalApplyRepository.findByUserAndLectureProposal(user, lectureProposal)
                        .orElseThrow(() -> new RuntimeException("신청된 강좌를 찾을 수 없습니다. userId : " + userId + ", proposalId : " + proposalId));

        lectureProposal.decreaseCurrent_participants();
        lectureProposalApplyRepository.delete(lectureProposalApply);

        return "강좌제안 신청이 취소되었습니다.";
    }
    
    // 강좌제안 신청자 목록
    
}
