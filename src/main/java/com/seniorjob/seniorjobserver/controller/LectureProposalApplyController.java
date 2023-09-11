package com.seniorjob.seniorjobserver.controller;

import com.seniorjob.seniorjobserver.domain.entity.LectureProposalApplyEntity;
import com.seniorjob.seniorjobserver.domain.entity.LectureProposalEntity;
import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import com.seniorjob.seniorjobserver.dto.LectureProposalApplyDto;
import com.seniorjob.seniorjobserver.repository.LectureProposalApplyRepository;
import com.seniorjob.seniorjobserver.repository.LectureProposalRepository;
import com.seniorjob.seniorjobserver.repository.UserRepository;
import com.seniorjob.seniorjobserver.service.LectureProposalApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lectureproposalapply")
public class LectureProposalApplyController {

    private final LectureProposalApplyService lectureProposalApplyService;
    private final UserRepository userRepository;
    private final LectureProposalRepository lectureProposalRepository;
    private final LectureProposalApplyRepository lectureProposalApplyRepository;

    @Autowired
    public LectureProposalApplyController(LectureProposalApplyService lectureProposalApplyService, UserRepository userRepository, LectureProposalRepository lectureProposalRepository, LectureProposalApplyRepository lectureProposalApplyRepository) {
        this.lectureProposalApplyService = lectureProposalApplyService;
        this.userRepository = userRepository;
        this.lectureProposalRepository = lectureProposalRepository;
        this.lectureProposalApplyRepository = lectureProposalApplyRepository;
    }

    // 강좌제안 참여신청 API
    // POST /apply/{proposal_id}/{applyReason}
    @PostMapping("/apply")
    public ResponseEntity<String> proposalApplyForLectyre(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long userId, @RequestParam Long proposal_id,
            @RequestParam(required = false) String applyReason) {
        try {
            UserEntity user = userRepository.findByPhoneNumber(userDetails.getUsername())
                            .orElseThrow(()-> new UsernameNotFoundException("유저를 찾을 수 없습니다.."));
            lectureProposalApplyService.applyForLectureProposal(user, proposal_id, applyReason);
            return ResponseEntity.ok("강좌제안 신청에 성공하였습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // 강좌제안 신청 취소API
    // DELETE /cancel/{proposal_id}
    @DeleteMapping("/cancel/{proposal_id}")
    public ResponseEntity<String> cancelLectureProposalApply(
            @PathVariable Long proposal_id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserEntity currentUser = userRepository.findByPhoneNumber(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

            LectureProposalEntity lectureProposal = lectureProposalRepository.findById(proposal_id)
                    .orElseThrow(() -> new RuntimeException("강좌제안을 찾을 수 없습니다."));

            boolean hasApplied = lectureProposalApplyRepository.existsByUserAndLectureProposal(currentUser, lectureProposal);
            if (!hasApplied) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("해당 강좌 제안글은 신청하지 않았습니다.");
            }

            lectureProposalApplyRepository.findByUserAndLectureProposal(currentUser, lectureProposal)
                    .ifPresent(lectureProposalApply -> {
                        lectureProposalApplyRepository.delete(lectureProposalApply);
                        lectureProposal.decreaseCurrent_participants();
                        lectureProposalRepository.save(lectureProposal);
                    });

            return ResponseEntity.ok("강좌 제안 신청이 취소되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 해당 강좌제안에 신청한 회원목록조회API
    // GET /list/{proposal_id}
    @GetMapping("/list")
    public ResponseEntity<List<LectureProposalApplyDto>> getApplicationByLectureProposalId(@RequestParam Long proposal_id) {
        List<LectureProposalApplyDto> applicants = lectureProposalApplyService.getApplicationByLectureProposalId(proposal_id);
        return ResponseEntity.ok(applicants);
    }

    // 강좌제안 신청 승인 상태 개별 변경 API
    // PUT /approve/{userId}/{status}/{lectureId}
    @PutMapping("/approve")
    public ResponseEntity<String> updateLectureProposalApplyStatus(
            @RequestParam Long userId,
            @RequestParam Long proposal_id,
            @RequestParam LectureProposalApplyEntity.LectureProposalApplyStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserEntity currentUser = userRepository.findByPhoneNumber(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

            LectureProposalEntity lectureProposal = lectureProposalRepository.findById(proposal_id)
                    .orElseThrow(() -> new RuntimeException("강좌제안글을 찾을 수 없습니다."));

            // 글 작성자만 변경 가능하도록 검증
            if (!lectureProposal.getUser().getUid().equals(currentUser.getUid())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("글 작성자만 상태를 변경할 수 있습니다.");
            }

            lectureProposalApplyService.updateLectureProposalApplyStatus(userId, proposal_id, status, currentUser.getUid());
            String message = String.format("회원의 강좌제안신청 상태를 변경하였습니다. userId : %d, proposal_id : %d, status : %s", userId, proposal_id, status);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 강좌제안 목록에서 승인이 된 회원들을 일괄 모집마감API
    // PUT /close/{proposal_id}
    @PutMapping("/close")
    public ResponseEntity<String> closeLectureProposalApply(
            @RequestParam("proposal_id") Long proposalId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserEntity currentUser = userRepository.findByPhoneNumber(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

            LectureProposalEntity lectureProposal = lectureProposalRepository.findById(proposalId)
                    .orElseThrow(() -> new RuntimeException("강좌제안글을 찾을 수 없습니다. proposalid : " + proposalId));

            if (!lectureProposal.getUser().getUid().equals(currentUser.getUid())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("글 작성자만 모집 마감할 수 있습니다.");
            }

            List<LectureProposalApplyEntity> approvedApplicants = lectureProposalApplyRepository.findByLectureProposalAndLectureProposalApplyStatus(lectureProposal, LectureProposalApplyEntity.LectureProposalApplyStatus.승인);

            if (approvedApplicants.isEmpty()) {
                return ResponseEntity.badRequest().body("해당 강좌제안에 승인된 회원이 없습니다. 강좌제안ID : " + proposalId);
            }

            lectureProposalApplyService.closeLectureProposalApply(proposalId, approvedApplicants);

            lectureProposal.setRecruitmentClosed(true);
            lectureProposalRepository.save(lectureProposal);
            // 승인된 회원 ID 리스트 생성
            List<Long> approvedUids = approvedApplicants.stream()
                    .map(applicant -> applicant.getUser().getUid())
                    .collect(Collectors.toList());

            return ResponseEntity.ok("강좌제안 모집마감이 완료되었습니다. 승인된 UID : " + approvedUids);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
