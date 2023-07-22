package com.seniorjob.seniorjobserver.controller;

import com.seniorjob.seniorjobserver.domain.entity.LectureApplyEntity;
import com.seniorjob.seniorjobserver.dto.LectureApplyDto;
import com.seniorjob.seniorjobserver.service.LectureApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class LectureApplyController {

    private final LectureApplyService lectureApplyService;

    @Autowired
    public LectureApplyController(LectureApplyService lectureApplyService) {
        this.lectureApplyService = lectureApplyService;
    }

    // 강좌 참여 신청 API
    // POST /api/lectureapply
    @PostMapping("/lectureapply")
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
    // DELETE /api/lectureapply
    @DeleteMapping("/lectureapply")
    public ResponseEntity<String> cancelLectureApply(@RequestParam Long userId, @RequestParam Long lectureId) {
        try {
            String message = lectureApplyService.cancelLectureApply(userId, lectureId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
