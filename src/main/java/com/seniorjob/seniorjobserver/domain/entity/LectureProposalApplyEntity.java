package com.seniorjob.seniorjobserver.domain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "lectureproposalapply")
public class LectureProposalApplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long le_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", referencedColumnName = "proposal_id")
    private LectureProposalEntity lectureProposal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    private UserEntity user;

    @Column(name = "apply_reason")
    private String applyReason;

    public enum LectureProposalApplyStatus {
        승인,
        대기
    }

    @Column(name = "lecture_proposal_apply_status")
    @Enumerated(EnumType.STRING)
    private LectureProposalApplyEntity.LectureProposalApplyStatus lectureProposalApplyStatus = LectureProposalApplyEntity.LectureProposalApplyStatus.대기;

    public LectureProposalApplyStatus getLectureProposalApplyStatus(){
        return lectureProposalApplyStatus;
    }

    public void setLectureProposalApplyStatus(LectureProposalApplyEntity.LectureProposalApplyStatus lectureProposalApplyStatus){
        this.lectureProposalApplyStatus = lectureProposalApplyStatus;
    }

    @Column(name = "recruitment_closed")
    private Boolean recruitmentClosed;

    @Column(name = "created_date", columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Builder
    public LectureProposalApplyEntity(Long le_id, LectureProposalEntity lectureProposal, UserEntity user, String applyReason,
                              LocalDateTime createdDate) {
        this.le_id = le_id;
        this.lectureProposal = lectureProposal;
        this.user = user;
        this.applyReason = applyReason;
        this.createdDate = createdDate;
    }
}
