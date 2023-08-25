package com.seniorjob.seniorjobserver.service;

import com.seniorjob.seniorjobserver.controller.LectureController;
import com.seniorjob.seniorjobserver.domain.entity.LectureEntity;
import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import com.seniorjob.seniorjobserver.dto.LectureDto;
import com.seniorjob.seniorjobserver.repository.LectureRepository;
import com.seniorjob.seniorjobserver.domain.entity.LectureEntity.LectureStatus;
import com.seniorjob.seniorjobserver.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LectureService {
    private final LectureRepository lectureRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(LectureController.class);

    // 모든강좌조회
    public LectureService(LectureRepository lectureRepository, UserRepository userRepository) {
        this.lectureRepository = lectureRepository;
        this.userRepository = userRepository;
    }

    public List<LectureDto> getAllLectures() {
        List<LectureEntity> lectureEntities = lectureRepository.findAll();
        return lectureEntities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        UserEntity user = userRepository.findByPhoneNumber(currentPrincipalName)
                .orElseThrow(() -> new RuntimeException("로그인된 사용자를 찾을 수 없습니다."));
        System.out.println("Current user: " + user); // or use a proper logger
        return user;
    }

    // 강좌개설
    public LectureDto createLecture(LectureDto lectureDto, UserEntity userEntity) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime startDate = lectureDto.getStart_date();
        LocalDateTime endDate = lectureDto.getEnd_date();
        LocalDateTime recruitEndDate = lectureDto.getRecruitEnd_date();
        LectureEntity lectureEntity = lectureDto.toEntity(userEntity);
        lectureEntity.setUser(userEntity);

        // 시작 날짜가 현재 날짜 이전인 경우, 예외
        if (startDate.isBefore(currentDate)) {
            throw new IllegalArgumentException("시작 날짜는 현재 날짜 이후로 설정해야 합니다.");
        }

        // 종료 날짜가 오늘 날짜 이후이고 시작 날짜 이후인 경우, 예외
        if (endDate.isBefore(currentDate) || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("종료 날짜는 오늘 이후의 날짜이고 시작 날짜 이후로 설정해야 합니다.");
        }

        // 강좌 모집 마감 날짜가 시작 날짜 이전인 경우, 예외
        if (recruitEndDate.isAfter(startDate)) {
            throw new IllegalArgumentException("강좌 모집 마감 날짜는 시작 날짜 이전으로 설정해야 합니다.");
        }
        // 강좌모집인원은 50명을 초과할수 없다. 초과할경우 예외
        if (lectureDto.getMax_participants() > 50) {
            throw new IllegalArgumentException("모집 인원은 50명을 초과할 수 없습니다.");
        }

        lectureEntity.setUser(userEntity);
        lectureEntity.updateStatus();
        LectureEntity savedLecture = lectureRepository.save(lectureEntity);

        return convertToDto(savedLecture);
    }

    // 로그인된 유저의 강좌수정
    public LectureDto updateLecture(Long create_id, LectureDto lectureDto) {
        LectureEntity existingLecture = lectureRepository.findById(create_id)
                .orElseThrow(() -> new RuntimeException("강좌아이디 찾지못함 create_id: " + create_id));

        existingLecture.setCreator(lectureDto.getCreator());
        existingLecture.setMaxParticipants(lectureDto.getMax_participants());
        existingLecture.setCurrentParticipants(lectureDto.getCurrent_participants());
        existingLecture.setCategory(lectureDto.getCategory());
        existingLecture.setBank_name(lectureDto.getBank_name());
        existingLecture.setAccount_name(lectureDto.getAccount_name());
        existingLecture.setAccount_number(lectureDto.getAccount_number());
        existingLecture.setPrice(lectureDto.getPrice());
        existingLecture.setTitle(lectureDto.getTitle());
        existingLecture.setContent(lectureDto.getContent());
        existingLecture.setCycle(lectureDto.getCycle());
        existingLecture.setCount(lectureDto.getCount());
        existingLecture.setStart_date(lectureDto.getStart_date());
        existingLecture.setEnd_date(lectureDto.getEnd_date());
        existingLecture.setRecruitEnd_date(lectureDto.getRecruitEnd_date());
        existingLecture.setRegion(lectureDto.getRegion());
        existingLecture.setImage_url(lectureDto.getImage_url());

        LectureEntity updatedLecture = lectureRepository.save(existingLecture);
        return convertToDto(updatedLecture);
    }

    // 강좌삭제
    public void deleteLecture(Long create_id) {
        lectureRepository.deleteById(create_id);
    }

    // 강좌상세보기
    public LectureDto getDetailLectureById(Long create_id) {
        LectureEntity lectureEntity = lectureRepository.findById(create_id)
                .orElseThrow(() -> new RuntimeException("강좌아이디 찾지못함 create_id: " + create_id));
        return convertToDto(lectureEntity);
    }

    // 세션로그인후 자신이 개설한 강좌목록 전체조회

    // 세션로그인후 자신이 개설한 강좌 상세보기

    // 강좌검색 : 제목
    public List<LectureDto> searchLecturesByTitle(String title) {
        List<LectureEntity> lectureEntities = lectureRepository.findByTitleContaining(title);
        return lectureEntities.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 강좌ID기반 강좌상태 가져오는 메서드
    public LectureEntity.LectureStatus getLectureStatus(Long create_id) {
        LectureEntity lectureEntity = lectureRepository.findById(create_id)
                .orElseThrow(() -> new RuntimeException("강좌아이디 찾지못함 create_id: " + create_id));
        return lectureEntity.getStatus();
    }

    // 강좌정렬
    // 최신순으로 강좌 정렬 최신 = true 오래된 = false
    public List<LectureDto> sortLecturesByCreatedDate(List<LectureDto> lectureList, boolean descending) {
        lectureList.sort((a, b) -> descending ?
                b.getCreatedDate().compareTo(a.getCreatedDate()) :
                a.getCreatedDate().compareTo(b.getCreatedDate()));
        return lectureList;
    }

    // 기존코드 인기순 : max_participant가많은순 -> 강좌 참여하기를 만들때 실제참여자가 많은순으로 변경할것임
    // 수정된 인기순 : 강좌에 참여한 사람이 많은 강좌순 : 참여자수 current_participants
    public List<LectureDto> sortLecturesByPopularity(List<LectureDto> lectureList, boolean descending) {
        lectureList.sort((a, b) -> descending ?
                b.getCurrent_participants() - a.getCurrent_participants() :
                a.getCurrent_participants() - b.getCurrent_participants());
        return lectureList;
    }

    // 가격순 : prices(낮은순 높은순)
    public List<LectureDto> sortLecturesByPrice(List<LectureDto> lectureList, boolean descending) {
        lectureList.sort((a, b) -> descending ?
                b.getPrice().compareTo(a.getPrice()) :
                a.getPrice().compareTo(b.getPrice()));
        return lectureList;
    }

    private LectureDto convertToDto(LectureEntity lectureEntity) {
        return LectureDto.builder()
                .create_id(lectureEntity.getCreate_id())

                .creator(lectureEntity.getCreator())
                .max_participants(lectureEntity.getMaxParticipants())
                .current_participants(lectureEntity.getCurrentParticipants())
                .category(lectureEntity.getCategory())
                .bank_name(lectureEntity.getBank_name())
                .account_name(lectureEntity.getAccount_name())
                .account_number(lectureEntity.getAccount_number())
                .price(lectureEntity.getPrice())
                .title(lectureEntity.getTitle())
                .content(lectureEntity.getContent())
                .cycle(lectureEntity.getCycle())
                .count(lectureEntity.getCount())
                .start_date(lectureEntity.getStart_date())
                .end_date(lectureEntity.getEnd_date())
                .region(lectureEntity.getRegion())
                .status(lectureEntity.getStatus())
                .image_url(lectureEntity.getImage_url())
                .createdDate(lectureEntity.getCreatedDate())
                .recruitEnd_date(lectureEntity.getRecruitEnd_date())
                .build();
    }

    //페이징
    public Page<LectureEntity> getLectures(Pageable pageable) {
        return lectureRepository.findAll(pageable);
    }

    // 강좌상태
    // 강좌 모집 마감 기능
    public void closeRecruitment(Long lectureId) {
        LectureEntity lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("강좌를 찾을 수 없습니다. id: " + lectureId));

        // 이미 모집 마감되었거나 시작된 강좌인 경우 예외 처리
        if (lecture.getStatus() == LectureStatus.개설대기상태 || lecture.getStatus() == LectureStatus.진행상태) {
            throw new RuntimeException("이미 모집 마감되었거나 진행 중인 강좌입니다.");
        }

        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.isAfter(lecture.getRecruitEnd_date())) {
            throw new RuntimeException("모집 마감일이 지났습니다. 강좌를 진행 상태로 변경할 수 없습니다.");
        }

        lecture.setStatus(LectureStatus.진행상태); // 강좌 상태를 진행 중으로 변경
        lectureRepository.save(lecture);
    }
}
