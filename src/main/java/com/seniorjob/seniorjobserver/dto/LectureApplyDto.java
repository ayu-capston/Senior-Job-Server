package com.seniorjob.seniorjobserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seniorjob.seniorjobserver.domain.entity.LectureApplyEntity;
import com.seniorjob.seniorjobserver.domain.entity.LectureEntity;
import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LectureApplyDto {
    @JsonIgnore
    private Long leId;

    @JsonIgnore
    private LectureEntity lecture;

    @JsonIgnore
    private UserEntity user;

    private String userName;
    private String applyReason;

    @JsonIgnore
    private LocalDateTime createdDate;

    public LectureApplyDto(LectureApplyEntity lectureApply) {
        this.leId = lectureApply.getLeId();
        this.applyReason = lectureApply.getApplyReason();
        this.createdDate = lectureApply.getCreatedDate();
        this.userName = lectureApply.getUser().getName();
    }

    @JsonIgnore
    public LectureApplyEntity toEntity() {
        return LectureApplyEntity.builder()
                .leId(leId)
                .applyReason(applyReason)
                .createdDate(createdDate)
                .build();
    }

    @Builder
    public LectureApplyDto(Long leId, LectureEntity lecture, UserEntity user, String userName, String applyReason,
                           LocalDateTime createdDate) {
        this.leId = leId;
        this.lecture = lecture;
        this.user = user;
        this.userName = userName;
        this.applyReason = applyReason;
        this.createdDate = createdDate;
    }
}
