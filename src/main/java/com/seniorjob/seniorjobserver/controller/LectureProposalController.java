package com.seniorjob.seniorjobserver.controller;

import com.seniorjob.seniorjobserver.domain.entity.LectureEntity;
import com.seniorjob.seniorjobserver.domain.entity.LectureProposalEntity;
import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import com.seniorjob.seniorjobserver.dto.LectureApplyDto;
import com.seniorjob.seniorjobserver.dto.LectureDto;
import com.seniorjob.seniorjobserver.dto.LectureProposalDto;
import com.seniorjob.seniorjobserver.repository.LectureProposalRepository;
import com.seniorjob.seniorjobserver.repository.UserRepository;
import com.seniorjob.seniorjobserver.service.LectureProposalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lectureproposal")
public class LectureProposalController {

    private final LectureProposalService lectureProposalService;
    private final UserRepository userRepository;
    private final LectureProposalRepository lectureProposalRepository;

    @Autowired
    public LectureProposalController(LectureProposalService lectureProposalService, UserRepository userRepository, LectureProposalRepository lectureProposalRepository){
        this.lectureProposalService = lectureProposalService;
        this.userRepository = userRepository;
        this.lectureProposalRepository = lectureProposalRepository;
    }

    // 강좌제안API
    // POST /api/lecturesproposal/apply
    @PostMapping("/apply")
    public ResponseEntity<LectureProposalDto> createLectureProposal(
            @RequestBody LectureProposalDto lectureProposalDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserEntity currentUser = userRepository.findByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        // 강좌 제안 개설 요청 처리
        LectureProposalDto createdProposal = lectureProposalService.createLectureProposal(currentUser, lectureProposalDto);

        return ResponseEntity.ok(createdProposal);
    }

    // 강좌제안 전체 목록API
    // GET /api/lectureproposal/all
    @GetMapping("/all")
    public List<LectureProposalDto> getAllProposals() {
        return lectureProposalService.getAllProposals();
    }

    // 강좌제안 상세보기API
    // GET /api/lectureproposal/detail/{proposal_id}
    @GetMapping("/detail/{proposal_id}")
    public ResponseEntity<LectureProposalDto> getLectureProposalDetail(@PathVariable Long proposal_id){
        LectureProposalDto lectureProposal = lectureProposalService.getDetail(proposal_id);
        return ResponseEntity.ok(lectureProposal);
    }

    // 로그인된 유저의 개설된 강좌제안 수정API
    // PUT /api/lectureproposal/
    @PutMapping("/update/{proposal_id}")
    public ResponseEntity<LectureProposalDto> updateLectureProposal(
            @PathVariable Long proposal_id,
            @RequestBody LectureProposalDto lectureProposalDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        UserEntity currentUser = userRepository.findByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        // 현재 사용자가 강좌제안의 생성자인지 확인
        LectureProposalEntity lectureProposal = lectureProposalRepository.findById(proposal_id)
                .orElseThrow(() -> new RuntimeException("강좌제안 아이디 찾지못함 proposal_id: " + proposal_id));
        if (!lectureProposal.getUser().equals(currentUser)) {
            throw new RuntimeException("해당 강좌제안를 수정할 권한이 없습니다.");
        }

        LectureProposalDto updatedProposal = lectureProposalService.updateLectureProposal(currentUser, proposal_id, lectureProposalDto);
        return ResponseEntity.ok(updatedProposal);
    }

    // 강좌제안 삭제API
    // DELETE /api/lectureproposal/delete/{proposal_id}
    @DeleteMapping("/delete/{proposal_id}")
    public ResponseEntity<String> deleteLectureProposal(
            @PathVariable Long proposal_id, @AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = userRepository.findByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        String responseMessage = lectureProposalService.deleteLectureProposal(user, proposal_id);
        return ResponseEntity.ok(responseMessage);
    }
}