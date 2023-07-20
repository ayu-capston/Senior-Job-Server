package com.seniorjob.seniorjobserver.dto;

import com.seniorjob.seniorjobserver.domain.entity.LectureApplyEntity;
import com.seniorjob.seniorjobserver.domain.entity.LectureEntity;
import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LectureApplyDto {
    private Long leId;
    private LectureEntity lecture;
    private UserEntity user;
    private String applyReason;
    private LocalDateTime createdDate;

    public LectureApplyDto(LectureApplyEntity lectureApply) {
        this.leId = lectureApply.getLeId();
        this.applyReason = lectureApply.getApplyReason();
        this.createdDate = lectureApply.getCreatedDate();
    }

    public LectureApplyEntity toEntity() {
        return LectureApplyEntity.builder()
                .leId(leId)
                .lecture(lecture)
                .user(user)
                .applyReason(applyReason)
                .createdDate(createdDate)
                .build();
    }

    @Builder
    public LectureApplyDto(Long leId, LectureEntity lecture, UserEntity user, String applyReason,
                           LocalDateTime createdDate) {
        this.leId = leId;
        this.lecture = lecture;
        this.user = user;
        this.applyReason = applyReason;
        this.createdDate = createdDate;
    }
}
