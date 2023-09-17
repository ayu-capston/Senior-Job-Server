package com.seniorjob.seniorjobserver.controller;

import com.seniorjob.seniorjobserver.dto.LectureProposalDto;
import com.seniorjob.seniorjobserver.repository.LectureRepository;
import com.seniorjob.seniorjobserver.service.LectureProposalService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import com.seniorjob.seniorjobserver.dto.LectureDto;
import com.seniorjob.seniorjobserver.repository.UserRepository;
import com.seniorjob.seniorjobserver.service.LectureService;
import com.seniorjob.seniorjobserver.service.StorageService;
import com.seniorjob.seniorjobserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
public class MypageController {
    private final LectureService lectureService;
    private final StorageService storageService;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(LectureController.class);
    private final LectureProposalService lectureProposalService;

    public MypageController(LectureService lectureService, StorageService storageService, UserRepository userRepository, UserService userService, LectureRepository lectureRepository ,LectureProposalService lectureProposalService) {
        this.lectureService = lectureService;
        this.storageService = storageService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.lectureRepository = lectureRepository;
        this.lectureProposalService = lectureProposalService;
    }

    // 세션로그인후 자신이 개설한 강좌목록 전체조회API - 회원으로 이동 (개설강좌)
    @GetMapping("/myLectureAll")
    public ResponseEntity<?> getMyLectureAll(@AuthenticationPrincipal UserDetails userDetails) {
        UserEntity currentUser = userRepository.findByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
        List<LectureDto> myLectureAll = lectureService.getMyLectureAll(currentUser.getUid());

        if (myLectureAll.isEmpty()) {
            return ResponseEntity.ok("개설된 강좌가 없습니다.");
        }

        return ResponseEntity.ok(myLectureAll);
    }

    // 세션로그인후 자신이 개설한 강좌제안 글 전체 조화 API
    @GetMapping("/myProposalAll")
    public ResponseEntity<?> getMyProposalAll(@AuthenticationPrincipal UserDetails userDetails) {
        UserEntity currentUser = userRepository.findByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));
        List<LectureProposalDto> myProposalAll = lectureProposalService.getMyProposalAll(currentUser.getUid());

        if (myProposalAll.isEmpty()) {
            return ResponseEntity.ok("제안된 강좌가 없습니다.");
        }

        return ResponseEntity.ok(myProposalAll);
    }

}
