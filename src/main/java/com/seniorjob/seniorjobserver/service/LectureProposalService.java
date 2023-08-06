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

@Service
public class LectureProposalService {

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
        if(startDate.isBefore(currentDate)){
            throw new IllegalArgumentException("시작날짜는 현재 날짜 이후로 설정해야 합니다.");
        }

        // 강좌제안개설을 할때 제안하는 종료 날짜는 희망날짜 이후여야한다.
        if(endDate.isBefore(currentDate) || endDate.isBefore(startDate)){
            throw new IllegalArgumentException("종료날짜는 오늘 이후 날짜이고 시작날짜 이후로 설정해야 합니다.");
        }

        LectureProposalEntity lectureProposalEntity = lectureProposalDto.toEntity();
        lectureProposalEntity.setUser(user);
        lectureProposalEntity.setCreated_date(currentDate);

        LectureProposalEntity savedLectureProposal = lectureProposalRepository.save(lectureProposalEntity);

        return LectureProposalDto.convertToDto(savedLectureProposal);
    }
}
