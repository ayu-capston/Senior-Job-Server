package com.seniorjob.seniorjobserver.controller;

import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import com.seniorjob.seniorjobserver.dto.LectureProposalDto;
import com.seniorjob.seniorjobserver.repository.UserRepository;
import com.seniorjob.seniorjobserver.service.LectureProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lectureproposal")
public class LectureProposalController {

    private final LectureProposalService lectureProposalService;
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

    // 강좌제안 상세보기API
    // GET /api/lectureproposal/{proposal_id}
    @GetMapping("/{proposal_id}")
    public ResponseEntity<LectureProposalDto> getLectureProposalDetail(@PathVariable Long proposal_id){
        LectureProposalDto lectureProposal = lectureProposalService.getDetail(proposal_id);
        return ResponseEntity.ok(lectureProposal);
    }
}
