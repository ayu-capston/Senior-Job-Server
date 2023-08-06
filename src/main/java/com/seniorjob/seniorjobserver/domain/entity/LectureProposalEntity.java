package com.seniorjob.seniorjobserver.domain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
@Table(name = "Lectureproposal")
public class LectureProposalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proposal_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", referencedColumnName = "uid")
    private UserEntity user;

    @Column(name = "title")
    private String title;

    @Column(name = "category")
    private String category;

    @Column(name = "start_date", columnDefinition = "datetime")
    private LocalDateTime start_date;

    @Column(name = "end_date", columnDefinition = "datetime")
    private LocalDateTime end_date;

    @Column(name = "region")
    private String region;

    @Column(name = "price")
    private int price;

    @Column(name = "content")
    private String content;

    @Column(name = "current_participants")
    private Integer current_participants;

    @Column(name = "created_date", columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP", nullable = false)
    @CreatedDate
    private LocalDateTime created_date;

    @Builder
    public LectureProposalEntity(Long proposal_id, UserEntity user, String title, String category, String region, LocalDateTime start_date, LocalDateTime end_date,
                                 Integer price, String content, Integer currentParticipants, LocalDateTime created_date) {
        this.proposal_id = proposal_id;
        this.user = user;
        this.title = title;
        this.category = category;
        this.start_date = start_date;
        this.end_date = end_date;
        this.region = region;
        this.price = price;
        this.content = content;
        this.current_participants = currentParticipants;
        this.created_date = created_date;
    }

    public void increaseCurrentParticipants() {
        if (current_participants == null) {
            current_participants = 0;
        }
        current_participants++;
    }
    public void decreaseCurrent_participants() {
        if (current_participants != null && current_participants > 0) {
            current_participants--;
        }
    }
}
