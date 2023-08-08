package com.seniorjob.seniorjobserver.controller;

import com.seniorjob.seniorjobserver.service.LectureProposalApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lectureproposalapply")
public class LectureProposalApplyController {

    private final LectureProposalApplyService lectureProposalApplyService;

    @Autowired
    public LectureProposalApplyController(LectureProposalApplyService lectureProposalApplyService){
        this.lectureProposalApplyService = lectureProposalApplyService;
    }

    // 강좌제안 참여신청 API
    // POST /{uid}/{proposal_id}/{applyReason}
    @PostMapping("")
    public ResponseEntity<String> proposalApplyForLectyre(@RequestParam Long userId, @RequestParam Long proposal_id, @RequestParam(required = false) String applyReason){
        try {
            lectureProposalApplyService.applyForLectureProposal(userId, proposal_id, applyReason);
            return ResponseEntity.ok("강좌제안 신청에 성공하였습니다.");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 강좌제안 신청 취소 API
    // DELETE /{userId}/{proposal_id}
    @DeleteMapping()
    public ResponseEntity<String> cancelLectureProposalApply(@RequestParam Long userId, @RequestParam Long proposal_id){
        try{
            String message = lectureProposalApplyService.cancelLectureProposalApply(userId, proposal_id);
            return ResponseEntity.ok(message);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
