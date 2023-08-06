package com.seniorjob.seniorjobserver.dto;

import com.seniorjob.seniorjobserver.domain.entity.LectureEntity;
import com.seniorjob.seniorjobserver.domain.entity.LectureProposalEntity;
import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LectureProposalDto {

    private Long proposalId;
    private UserEntity user;
    private String title;
    private String category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String region;
    private Integer price;
    private String content;
    private LocalDateTime createDate;

    public LectureProposalEntity toEntity() {
        return LectureProposalEntity.builder()
                .proposal_id(proposalId)
                .user(user)
                .title(title)
                .category(category)
                .start_date(startDate)
                .end_date(endDate)
                .region(region)
                .price(price)
                .content(content)
                .created_date(createDate)
                .build();
    }

    @Builder
    public LectureProposalDto(Long proposalId, UserEntity user, String title,
                              String category, LocalDateTime startDate, LocalDateTime endDate, String region,
                              Integer price, String content, LocalDateTime createDate){
        this.proposalId = proposalId;
        this.user = user;
        this.title = title;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.region = region;
        this.price = price;
        this.content = content;
        this.createDate = createDate;
    }

    public static LectureProposalDto convertToDto(LectureProposalEntity lectureProposalEntity) {
        return LectureProposalDto.builder()
                .proposalId(lectureProposalEntity.getProposal_id())
                .user(lectureProposalEntity.getUser())
                .title(lectureProposalEntity.getTitle())
                .category(lectureProposalEntity.getCategory())
                .startDate(lectureProposalEntity.getStart_date())
                .endDate(lectureProposalEntity.getEnd_date())
                .region(lectureProposalEntity.getRegion())
                .price(lectureProposalEntity.getPrice())
                .content(lectureProposalEntity.getContent())
                .createDate(lectureProposalEntity.getCreated_date())
                .build();
    }
}
