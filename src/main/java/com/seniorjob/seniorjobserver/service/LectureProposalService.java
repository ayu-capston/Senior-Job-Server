package com.seniorjob.seniorjobserver.service;

import com.seniorjob.seniorjobserver.domain.entity.LectureEntity;
import com.seniorjob.seniorjobserver.domain.entity.LectureProposalEntity;
import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import com.seniorjob.seniorjobserver.dto.LectureProposalDto;
import com.seniorjob.seniorjobserver.repository.LectureProposalRepository;
import com.seniorjob.seniorjobserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LectureProposalService {

    @Autowired
    private final LectureProposalRepository lectureProposalRepository;
    private final UserRepository userRepository;

    @Autowired
    public LectureProposalService(LectureProposalRepository lectureProposalRepository, UserRepository userRepository) {
        this.lectureProposalRepository = lectureProposalRepository;
        this.userRepository = userRepository;
    }

    // 강좌제안개설
    public LectureProposalDto createLectureProposal(UserEntity user, LectureProposalDto lectureProposalDto) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime startDate = lectureProposalDto.getStartDate();
        LocalDateTime endDate = lectureProposalDto.getEndDate();

        // 강좌제안개설을 할때 제안하는 희망 날짜는 현재날짜보다 이후여야한다.
        if (startDate.isBefore(currentDate)) {
            throw new IllegalArgumentException("시작날짜는 현재 날짜 이후로 설정해야 합니다.");
        }

        // 강좌제안개설을 할때 제안하는 종료 날짜는 희망날짜 이후여야한다.
        if (endDate.isBefore(currentDate) || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("종료날짜는 오늘 이후 날짜이고 시작날짜 이후로 설정해야 합니다.");
        }

        LectureProposalEntity lectureProposalEntity = lectureProposalDto.toEntity();
        lectureProposalEntity.setUser(user);
        lectureProposalEntity.setCreated_date(currentDate);

        LectureProposalEntity savedLectureProposal = lectureProposalRepository.save(lectureProposalEntity);

        return LectureProposalDto.convertToDto(savedLectureProposal);
    }

    // 제안된강좌 전체목록 조회
    public List<LectureProposalDto> getAllProposals() {
        List<LectureProposalEntity> lectureProposals = lectureProposalRepository.findAll();

        return lectureProposals.stream()
                .map(LectureProposalDto::new)
                .collect(Collectors.toList());
    }

    // 제안된 강좌 수정
    public LectureProposalDto updateLectureProposal(UserEntity user, Long proposal_id, LectureProposalDto lectureProposalDto) {

        LectureProposalEntity lectureProposal = lectureProposalRepository.findById(proposal_id)
                .orElseThrow(() -> new RuntimeException("해당 강좌 제안을 찾을 수 없습니다. ID: " + proposal_id));

        if (!lectureProposal.getUser().getUid().equals(user.getUid())) {
            throw new IllegalArgumentException("강좌제안 개설자와 일치하지 않습니다.");
        }

        LocalDateTime currentDate = LocalDateTime.now();

        if (lectureProposalDto.getStartDate().isBefore(currentDate)) {
            throw new IllegalArgumentException("시작날짜는 현재 날짜 이후로 설정해야 합니다.");
        }

        if (lectureProposalDto.getEndDate().isBefore(currentDate) || lectureProposalDto.getEndDate().isBefore(lectureProposalDto.getStartDate())) {
            throw new IllegalArgumentException("종료날짜는 오늘 이후 날짜이고 시작날짜 이후로 설정해야 합니다.");
        }

        lectureProposal.setTitle(lectureProposalDto.getTitle());
        lectureProposal.setCategory(lectureProposalDto.getCategory());
        lectureProposal.setStart_date(lectureProposalDto.getStartDate());
        lectureProposal.setEnd_date(lectureProposalDto.getEndDate());
        lectureProposal.setRegion(lectureProposalDto.getRegion());
        lectureProposal.setPrice(lectureProposalDto.getPrice());
        lectureProposal.setContent(lectureProposalDto.getContent());

        LectureProposalEntity updatedProposal = lectureProposalRepository.save(lectureProposal);
        return new LectureProposalDto(updatedProposal);
    }


    // 제안된강좌 삭제
    public String deleteLectureProposal(UserEntity user, Long proposal_id) {
        LectureProposalEntity lectureProposal = lectureProposalRepository.findById(proposal_id)
                .orElseThrow(() -> new RuntimeException("해당 강좌 제안을 찾을 수 없습니다. ID: " + proposal_id));

        // 사용자 확인
        if (!lectureProposal.getUser().getUid().equals(user.getUid())) {
            throw new IllegalArgumentException("강좌제안 개설자만 해당 강좌 제안을 삭제할 수 있습니다.");
        }

        lectureProposalRepository.deleteById(proposal_id);
        return proposal_id + "를 삭제하였습니다.";
    }
}
