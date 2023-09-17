package com.seniorjob;

//import com.seniorjob.seniorjobserver.domain.entity.LectureEntity;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//
//import static junit.framework.TestCase.assertEquals;
//
//@SpringBootTest(classes = SeniorjobApplication.class)
//public class LectureEntityTest {
//
//    private LectureEntity lectureEntity;
//    private LocalDateTime fixedNow;
//
//    @BeforeEach
//    public void setUp() {
//        fixedNow = LocalDateTime.of(2023, 9, 13, 10, 0); // 고정된 시간 값
//        lectureEntity = new LectureEntity() {
//
//            @Override
//            protected LocalDateTime getNow() {
//                return fixedNow; // 테스트 케이스에서 사용하는 시간을 반환
//            }
//        };
//    }
//
//    @Test
//    public void testCreateLectureAsAvailableStatus() {
//        // 강좌 정보 설정
//        lectureEntity.setRecruitEnd_date(fixedNow.plusDays(1));
//        lectureEntity.setStart_date(fixedNow.plusDays(2));
//        lectureEntity.setEnd_date(fixedNow.plusDays(3));
//        lectureEntity.setMaxParticipants(10);
//        lectureEntity.setCurrentParticipants(0);
//
//        lectureEntity.updateStatus();
//
//        assertEquals(LectureEntity.LectureStatus.신청가능상태, lectureEntity.getStatus());
//    }
//    @Test
//    public void testCreateLectureAsWaitingStatus() {
//        LocalDateTime fixedNow = LocalDateTime.of(2023, 9, 13, 10, 0); // 고정된 시간 값
//
//        // 강좌 정보 설정
//        lectureEntity.setRecruitEnd_date(fixedNow.minusDays(1)); // 모집이 이미 마감되었음
//        lectureEntity.setStart_date(fixedNow.plusDays(2)); // 강좌 시작일이 아직 오지 않았음
//        lectureEntity.setEnd_date(fixedNow.plusDays(3));  // 강좌 종료일이 아직 오지 않았음
//        lectureEntity.setMaxParticipants(10);
//        lectureEntity.setCurrentParticipants(10); // 현재 참가자 수를 최대 참가자 수와 동일하게 설정
//
//        lectureEntity.updateStatus();
//
//        assertEquals(LectureEntity.LectureStatus.개설대기상태, lectureEntity.getStatus());
//    }
//    @Test
//    public void testCreateLectureAsInProgressStatus() {
//        fixedNow = LocalDateTime.of(2023, 9, 14, 10, 0); // 클래스 레벨 변수의 값을 변경
//
//        // 강좌 정보 설정
//        lectureEntity.setRecruitEnd_date(fixedNow.minusDays(2)); // 모집 마감일이 이미 지났음
//        lectureEntity.setStart_date(fixedNow.minusDays(1)); // 강좌가 이미 시작함
//        lectureEntity.setEnd_date(fixedNow.plusDays(3)); // 강좌 종료일은 아직 오지 않음
//        lectureEntity.setMaxParticipants(10);
//        lectureEntity.setCurrentParticipants(5);
//        lectureEntity.setRecruitmentClosed(true); // 모집을 마감으로 설정
//
//        lectureEntity.updateStatus();
//
//        assertEquals(LectureEntity.LectureStatus.진행상태, lectureEntity.getStatus());
//    }
//
//    @Test
//    public void testCreateLectureAsWithdrawnStatus() {
//        fixedNow = LocalDateTime.of(2023, 11, 14, 10, 0); // 고정된 시간 값을 클래스 레벨 변수에 설정
//
//        // 강좌 정보 설정
//        lectureEntity.setRecruitEnd_date(fixedNow.minusDays(1)); // 모집 마감일이 이미 지났음
//        lectureEntity.setStart_date(fixedNow.plusDays(1)); // 강좌 시작일이 아직 오지 않았음 (하지만 곧 시작됨)
//        lectureEntity.setEnd_date(fixedNow.plusDays(3)); // 강좌 종료일은 아직 오지 않음
//        lectureEntity.setMaxParticipants(10);
//        lectureEntity.setCurrentParticipants(5);
//        lectureEntity.setRecruitmentClosed(false); // 모집이 아직 마감되지 않았음
//
//        lectureEntity.updateStatus();
//
//        assertEquals(LectureEntity.LectureStatus.철회상태, lectureEntity.getStatus());
//    }
//
//
//    @Test
//    public void testCreateLectureAsCompletedStatus() {
//        fixedNow = LocalDateTime.of(2023, 9, 14, 10, 0); // 클래스 레벨 변수의 값을 변경
//
//        // 강좌 정보 설정
//        lectureEntity.setRecruitEnd_date(fixedNow.minusDays(10)); // 모집 마감일이 이미 지났음
//        lectureEntity.setStart_date(fixedNow.minusDays(9)); // 강좌 시작일도 이미 지났음
//        lectureEntity.setEnd_date(fixedNow.minusDays(1)); // 강좌 종료일도 이미 지났음
//        lectureEntity.setMaxParticipants(10);
//        lectureEntity.setCurrentParticipants(5);
//        lectureEntity.setRecruitmentClosed(true); // 모집을 마감으로 설정
//
//        lectureEntity.updateStatus();
//
//        assertEquals(LectureEntity.LectureStatus.완료상태, lectureEntity.getStatus());
//    }
//}
