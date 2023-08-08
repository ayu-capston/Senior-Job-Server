package com.seniorjob.seniorjobserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seniorjob.seniorjobserver.domain.entity.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LectureProposalApplyDto {
    @JsonIgnore
    private Long le_Id;
    @JsonIgnore
    private LectureProposalEntity lectureProposal;
    @JsonIgnore
    private UserEntity user;
    private String userName;
    private String applyReason;
    private LectureProposalApplyEntity.LectureProposalApplyStatus lectureProposalApplyStatus;
    @JsonIgnore
    private LocalDateTime createdDate;
    @JsonIgnore
    private Boolean recruitmentClosed;

    public LectureProposalApplyDto(LectureProposalApplyEntity lectureProposalApply) {
        this.le_Id = lectureProposalApply.getLe_id();
        this.applyReason = lectureProposalApply.getApplyReason();
        this.createdDate = lectureProposalApply.getCreatedDate();
        this.userName = lectureProposalApply.getUser().getName();
        this.lectureProposalApplyStatus = lectureProposalApply.getLectureProposalApplyStatus();
        this.recruitmentClosed = lectureProposalApply.getRecruitmentClosed();
    }

    public LectureApplyEntity toEntity() {
        return LectureApplyEntity.builder()
                .leId(le_Id)
                .applyReason(applyReason)
                .createdDate(createdDate)
                .build();
    }

    @Builder
    public LectureProposalApplyDto(Long le_id, LectureProposalEntity lectureProposal, UserEntity user, String userName, String applyReason,
                           LocalDateTime createdDate, LectureProposalApplyEntity.LectureProposalApplyStatus lectureProposalApplyStatus) {
        this.le_Id = le_id;
        this.lectureProposal = lectureProposal;
        this.user = user;
        this.userName = userName;
        this.applyReason = applyReason;
        this.createdDate = createdDate;
        this.lectureProposalApplyStatus = lectureProposalApplyStatus;
    }
}
