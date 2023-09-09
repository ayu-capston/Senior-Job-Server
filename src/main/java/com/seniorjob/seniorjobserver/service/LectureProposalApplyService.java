package com.seniorjob.seniorjobserver.service;


import com.seniorjob.seniorjobserver.domain.entity.LectureProposalApplyEntity;
import com.seniorjob.seniorjobserver.domain.entity.LectureProposalEntity;
import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import com.seniorjob.seniorjobserver.dto.LectureProposalApplyDto;
import com.seniorjob.seniorjobserver.repository.LectureProposalApplyRepository;
import com.seniorjob.seniorjobserver.repository.LectureProposalRepository;
import com.seniorjob.seniorjobserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
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
    public void applyForLectureProposal(UserEntity userId, Long proposal_id, String applyReason){
        UserEntity user = userRepository.findById(userId.getUid())
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

    // 강좌제안 신청취소
    public String cancelLectureProposalApply(Long userId, Long proposalId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. userid : " + userId));

        LectureProposalEntity lectureProposal = lectureProposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("강좌제안를 찾을 수 없습니다. proposalid : " + proposalId));

        LectureProposalApplyEntity lectureProposalApply =
                lectureProposalApplyRepository.findByUserAndLectureProposal(user, lectureProposal)
                        .orElseThrow(() -> new RuntimeException("신청된 강좌를 찾을 수 없습니다. userId : " + userId + ", proposalId : " + proposalId));

        lectureProposal.decreaseCurrent_participants();
        lectureProposalApplyRepository.delete(lectureProposalApply);

        return "강좌제안 신청이 취소되었습니다.";
    }

    // 해당 강좌제안에 신청한 회원 목록 조회
    public List<LectureProposalApplyDto> getApplicationByLectureProposalId(Long proposal_id){
        LectureProposalEntity lectureProposal = lectureProposalRepository.findById(proposal_id)
                .orElseThrow(() -> new RuntimeException("해당 강좌제안을 찾을 수 업습니다. proposal_id : " + proposal_id));

        List<LectureProposalApplyEntity> applications = lectureProposalApplyRepository.findByLectureProposal(lectureProposal);

        if(applications.isEmpty()){
            throw new RuntimeException("해당 강좌제안에 신청한 회원이 없습니다. 강좌제안ID : + " + proposal_id);
        }

        return applications.stream()
                .map(LectureProposalApplyDto::new)
                .collect(Collectors.toList());
    }

    // 강좌제안신청 승인 상태 개별 변경
    public void updateLectureProposalApplyStatus(Long userId, Long proposalId, LectureProposalApplyEntity.LectureProposalApplyStatus status, Long loggedInUserId){
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. userId : " + userId));

        LectureProposalEntity lectureProposal = lectureProposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("강좌제안를 찾을 수 없습니다. proposalid : " + proposalId));

        // 조건 추가: 해당 강좌 제안에 승인된 사용자가 있는지 확인
        List<LectureProposalApplyEntity> approvedApplications = lectureProposalApplyRepository.findByLectureProposalAndLectureProposalApplyStatus(lectureProposal, LectureProposalApplyEntity.LectureProposalApplyStatus.승인);
        if (!approvedApplications.isEmpty()) {
            throw new RuntimeException("해당 강좌제안에 이미 승인된 회원이 있습니다.");
        }

        LectureProposalApplyEntity lectureProposalApply =
                lectureProposalApplyRepository.findByUserAndLectureProposal(user, lectureProposal)
                        .orElseThrow(() -> new RuntimeException("회원의 신청한 강좌제안을 찾을 수 없습니다. userId : " + userId + ", proposalId : " + proposalId));

        lectureProposalApply.setLectureProposalApplyStatus(status);
        lectureProposalApplyRepository.save(lectureProposalApply);
    }

    // 강좌제안 목록에서 승인이 된 회원이 한명일 경우 모집마감
    @Transactional
    public void closeLectureProposalApply(Long proposalId, List<LectureProposalApplyEntity> approvedApplicants) {
        for (LectureProposalApplyEntity applicant : approvedApplicants) {
            applicant.setRecruitmentClosed(true);
            lectureProposalApplyRepository.save(applicant);
        }
    }
}
