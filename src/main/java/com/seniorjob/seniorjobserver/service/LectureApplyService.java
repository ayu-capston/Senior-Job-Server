package com.seniorjob.seniorjobserver.service;

import com.seniorjob.seniorjobserver.domain.entity.LectureApplyEntity;
import com.seniorjob.seniorjobserver.domain.entity.LectureEntity;
import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import com.seniorjob.seniorjobserver.dto.LectureApplyDto;
import com.seniorjob.seniorjobserver.repository.LectureApplyRepository;
import com.seniorjob.seniorjobserver.repository.LectureRepository;
import com.seniorjob.seniorjobserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LectureApplyService {

    private final LectureRepository lectureRepository;
    private final LectureApplyRepository lectureApplyRepository;
    private final UserRepository userRepository;

    @Autowired
    public LectureApplyService(UserRepository userRepository, LectureRepository lectureRepository, LectureApplyRepository lectureApplyRepository) {
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
        this.lectureApplyRepository = lectureApplyRepository;
    }

    public void applyForLecture(Long userId, Long lectureId, String applyReason) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. id: " + userId));

        LectureEntity lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다. id: " + lectureId));

        // 이미 강좌에 참여한 경우 예외 처리
        if (lectureApplyRepository.existsByUserAndLecture(user, lecture)) {
            throw new RuntimeException(lectureId + " 이미 참여하신 강좌입니다.");
        }

        // 강좌신청이유 필수! 신청이유가 없을 경우 예외처리
        if (applyReason == null || applyReason.isEmpty()) {
            throw new IllegalArgumentException("강좌신청이유를 작성해주세요!!");
        }

        // 강좌 참여 생성
        LectureApplyEntity lectureApply = LectureApplyEntity.builder()
                .lecture(lecture)
                .user(user)
                .createdDate(LocalDateTime.now())
                .applyReason(applyReason)
                .build();
        lecture.increaseCurrentParticipants();
        lectureApplyRepository.save(lectureApply);
    }

    // 강좌참여신청취소
    public String cancelLectureApply(Long userId, Long lectureId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        LectureEntity lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다. id: " + lectureId));

        LectureApplyEntity lectureApply = (LectureApplyEntity) lectureApplyRepository.findByUserAndLecture(user, lecture)
                .orElseThrow(() -> new RuntimeException("신청된 강좌를 찾을 수 없습니다. userId: " + userId + ", lectureId: " + lectureId));

        lecture.decreaseCurrentParticipants();
        lectureApplyRepository.delete(lectureApply);

        return "강좌 신청이 취소되었습니다.";
    }

    // 해당 강좌에 신청한 회원 목록 조회 메서드
    public List<LectureApplyDto> getApplicantsForLecture(Long lectureId) {
        LectureEntity lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다. id: " + lectureId));

        List<LectureApplyEntity> lectureApplies = lectureApplyRepository.findByLecture(lecture);
        return lectureApplies.stream()
                .map(LectureApplyDto::new)
                .collect(Collectors.toList());
    }

}
