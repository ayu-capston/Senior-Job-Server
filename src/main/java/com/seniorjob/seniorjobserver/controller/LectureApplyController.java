package com.seniorjob.seniorjobserver.controller;

import com.seniorjob.seniorjobserver.domain.entity.LectureApplyEntity;
import com.seniorjob.seniorjobserver.dto.LectureApplyDto;
import com.seniorjob.seniorjobserver.service.LectureApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/lectureapply")
public class LectureApplyController {

    private final LectureApplyService lectureApplyService;

    @Autowired
    public LectureApplyController(LectureApplyService lectureApplyService) {
        this.lectureApplyService = lectureApplyService;
    }

    // 강좌 참여 신청 API
    // POST /apply/{userId}/{lectureId}/{applyReason}
    @PostMapping("/apply")
    public ResponseEntity<String> applyForLecture(@RequestParam Long userId, @RequestParam Long lectureId, @RequestParam(required = false) String applyReason) {
        try {
            lectureApplyService.applyForLecture(userId, lectureId, applyReason);
            return ResponseEntity.ok("강좌 신청에 성공하였습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 강좌 신청 취소 API
    // DELETE /cancel/{userId}/{lectureId}
    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelLectureApply(@RequestParam Long userId, @RequestParam Long lectureId) {
        try {
            String message = lectureApplyService.cancelLectureApply(userId, lectureId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 해당 강좌에 신청한 회원 목록 조회 API
    // GET /list?lectureId=99
    @GetMapping("/list")
    public ResponseEntity<List<LectureApplyDto>> getApplicantsByLectureId(@RequestParam("lectureId") Long lectureId) {
        List<LectureApplyDto> applicants = lectureApplyService.getApplicantsByLectureId(lectureId);
        return ResponseEntity.ok(applicants);
    }

    // 회원목록에서 승인이 된 회원들을 일괄 모집마감하는 API
    // PUT /close/{lectureId}
    @PutMapping("/close")
    public ResponseEntity<String> closeLectureApply(@RequestParam Long lectureId) {
        try {
            return lectureApplyService.closeLectureApply(lectureId);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // 강좌참여신청 승인 상태 개별 변경 API
    // PUT /approve/{userId}/{lectureId}/{status}
    @PutMapping("/approve")
    public ResponseEntity<String> updateLectureApplyStatus(
            @RequestParam Long userId,
            @RequestParam Long lectureId,
            @RequestParam LectureApplyEntity.LectureApplyStatus status) {
        try {
            lectureApplyService.updateLectureApplyStatus(userId, lectureId, status);
            String message = String.format("강좌참여신청을 변경하였습니다. userId: %d, lectureId: %d", userId, lectureId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
