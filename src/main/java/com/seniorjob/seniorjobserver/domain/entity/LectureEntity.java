package com.seniorjob.seniorjobserver.domain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "lecture")
public class LectureEntity extends TimeEntity {

    public enum LectureStatus {
        AVAILABLE,    // 신청 가능 상태
        WAITING,      // 개설 대기 상태
        ONGOING,      // 진행 상태
        WITHDRAWN,    // 철회 상태
        COMPLETED     // 완료 상태
    }

    public void updateStatus() {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(recruitEnd_date) && currentParticipants < maxParticipants) {
            status = LectureStatus.AVAILABLE;
        } else if (now.isBefore(start_date) && (now.isAfter(recruitEnd_date) || currentParticipants.equals(maxParticipants))) {
            status = LectureStatus.WAITING;
        } else if (now.isAfter(start_date) && now.isBefore(end_date)) {
            status = LectureStatus.ONGOING;
        } else if (now.isAfter(end_date)) {
            status = LectureStatus.COMPLETED;
        } else if (now.isAfter(recruitEnd_date) && status.equals(LectureStatus.AVAILABLE)) {
            status = LectureStatus.WITHDRAWN;
        }
    }

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long create_id;

    @Column(name = "creator", nullable = false)
    private String creator;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(name = "current_participants")
    private Integer currentParticipants;

    @Column(name = "category")
    private String category;

    @Column(name = "bank_name")
    private String bank_name;

    @Column(name = "account_name")
    private String account_name;

    @Column(name = "account_number")
    private String account_number;

    @Column(name = "price")
    private Integer price;

    @Column(name = "title")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "cycle")
    private String cycle;

    @Column(name = "count")
    private Integer count;

    @Column(name = "start_date", columnDefinition = "datetime")
    private LocalDateTime start_date;

    @Column(name = "end_date", columnDefinition = "datetime")
    private LocalDateTime end_date;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private LectureStatus status;

    @Column(name = "region")
    private String region;

    @Column(name = "image_url")
    private String image_url;

    @Column(name = "created_date", columnDefinition = "datetime DEFAULT CURRENT_TIMESTAMP", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "recruitEnd_date", columnDefinition = "datetime")
    private LocalDateTime recruitEnd_date;

    @Builder
    public LectureEntity(Long create_id, String creator, Integer maxParticipants, Integer currentParticipants, String category,
                         String bank_name, String account_name, String account_number, Integer price, String title, String content,
                         String cycle, Integer count, LocalDateTime start_date, LocalDateTime end_date, String region, String image_url,
                         LocalDateTime createdDate, LocalDateTime recruitEnd_date) {
        this.create_id = create_id;
        this.creator = creator;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = currentParticipants;
        this.category = category;
        this.bank_name = bank_name;
        this.account_name = account_name;
        this.account_number = account_number;
        this.price = price;
        this.title = title;
        this.content = content;
        this.cycle = cycle;
        this.count = count;
        this.start_date = start_date;
        this.end_date = end_date;
        this.region = region;
        this.image_url = image_url;
        this.createdDate = createdDate;
        this.recruitEnd_date = recruitEnd_date;
    }

    public void increaseCurrentParticipants() {
        if (currentParticipants == null) {
            currentParticipants = 0;
        }
        currentParticipants++;
    }
    public void decreaseCurrentParticipants() {
        if (currentParticipants != null && currentParticipants > 0) {
            currentParticipants--;
        }
    }
}
