package com.seniorjob.seniorjobserver.controller;

import com.seniorjob.seniorjobserver.domain.entity.LectureApplyEntity;
import com.seniorjob.seniorjobserver.domain.entity.LectureProposalApplyEntity;
import com.seniorjob.seniorjobserver.dto.LectureProposalApplyDto;
import com.seniorjob.seniorjobserver.service.LectureProposalApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lectureproposalapply")
public class LectureProposalApplyController {

    private final LectureProposalApplyService lectureProposalApplyService;

    @Autowired
    public LectureProposalApplyController(LectureProposalApplyService lectureProposalApplyService) {
        this.lectureProposalApplyService = lectureProposalApplyService;
    }

    // 강좌제안 참여신청 API
    // POST /apply/{uid}/{proposal_id}/{applyReason}
    @PostMapping("/apply")
    public ResponseEntity<String> proposalApplyForLectyre(@RequestParam Long userId, @RequestParam Long proposal_id, @RequestParam(required = false) String applyReason) {
        try {
            lectureProposalApplyService.applyForLectureProposal(userId, proposal_id, applyReason);
            return ResponseEntity.ok("강좌제안 신청에 성공하였습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 강좌제안 신청 취소API
    // DELETE /cancel/{userId}/{proposal_id}
    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelLectureProposalApply(@RequestParam Long userId, @RequestParam Long proposal_id){
        try{
            String message = lectureProposalApplyService.cancelLectureProposalApply(userId, proposal_id);
            return ResponseEntity.ok(message);
        }catch (RuntimeException e){
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
            @RequestParam LectureProposalApplyEntity.LectureProposalApplyStatus status) {
        try {
            lectureProposalApplyService.updateLectureProposalApplyStatus(userId, proposal_id, status);
            String message = String.format("강좌제안신청을 변경하였습니다. userId : %d, proposal_id : %d", userId, proposal_id);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 승인된 강좌제인 상태만 모집하는 API
    // PUT /close/{proposal_id}
    @PutMapping("/close")
    public ResponseEntity<String> closeLectureProposalApply(@RequestParam("proposal_id") Long proposalId){
        try{
            return lectureProposalApplyService.closeLectureProposalApply(proposalId);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}