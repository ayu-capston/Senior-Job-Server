package com.seniorjob.seniorjobserver.controller;

import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import com.seniorjob.seniorjobserver.dto.LectureProposalDto;
import com.seniorjob.seniorjobserver.repository.UserRepository;
import com.seniorjob.seniorjobserver.service.LectureProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lectureproposal")
public class LectureProposalController {

    @Autowired
    private final LectureProposalService lectureProposalService;
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    public LectureProposalController(LectureProposalService lectureProposalService, UserRepository userRepository){
        this.lectureProposalService = lectureProposalService;
        this.userRepository = userRepository;
    }

    // 강좌제안API
    // POST /api/lecturesproposal/{uid}
    @PostMapping("/{uid}")
    public ResponseEntity<LectureProposalDto> createLectureProposal(
            @PathVariable("uid") Long uid,
            @RequestBody LectureProposalDto lectureProposalDto
    ) {
        // uid로 회원 정보 조회
        UserEntity user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("해당 uid의 회원을 찾을 수 없습니다. uid: " + uid));

        // 강좌 제안 개설 요청 처리
        LectureProposalDto createdProposal = lectureProposalService.createLectureProposal(user, lectureProposalDto);

        return ResponseEntity.ok(createdProposal);
    }

    // 강좌제안 전체 목록API
    // GET /api/lectureproposal/all
    @GetMapping
    public List<LectureProposalDto> getAllProposals() {
        return lectureProposalService.getAllProposals();
    }

    // 강좌제안 수정API
    // PUT /api/lectureproposal/
    @PutMapping("/{userId}/{proposal_id}")
    public ResponseEntity<LectureProposalDto> updateLectureProposal(@PathVariable Long userId, @PathVariable Long proposal_id, @RequestBody LectureProposalDto lectureProposalDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));
        LectureProposalDto updatedProposal = lectureProposalService.updateLectureProposal(user, proposal_id, lectureProposalDto);
        return ResponseEntity.ok(updatedProposal);
    }

    // 강좌제안 삭제API
    // DELETE /api/lectureproposal/{userId}/{proposal_id}
    @DeleteMapping("/{userId}/{proposal_id}")
    public ResponseEntity<String> deleteLectureProposal(@PathVariable Long userId, @PathVariable Long proposal_id) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId));

        String responseMessage = lectureProposalService.deleteLectureProposal(user, proposal_id);
        return ResponseEntity.ok(responseMessage);
    }
}
