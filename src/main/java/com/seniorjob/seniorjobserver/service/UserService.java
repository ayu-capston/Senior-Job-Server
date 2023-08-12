package com.seniorjob.seniorjobserver.service;

import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import com.seniorjob.seniorjobserver.dto.UserDto;
import com.seniorjob.seniorjobserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입 encryption_code = 비밀번호 암호화
    public UserEntity createUser(UserDto userDto){

        String encryptedPassword = passwordEncoder.encode(userDto.getEncryptionCode());
        userDto.setEncryptionCode(encryptedPassword);

        if(existsByPhoneNumber(userDto.getPhoneNumber())){
            throw new IllegalArgumentException("이미 가입된 전화번호입니다.");
        }
        UserEntity userEntity = userDto.toEntity();
        return userRepository.save(userEntity);
    }

    private boolean existsByPhoneNumber(String phoneNumber){
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    // 회원 전체목록
    public List<UserDto> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        if (userEntities.isEmpty()) {
            throw new IllegalArgumentException("회원가입된 회원이 없습니다..");
        }
        return userEntities.stream()
                .map(this::convertToDo)
                .collect(Collectors.toList());
    }

    private UserDto convertToDo(UserEntity userEntity) {
        return UserDto.builder()
                .uid(userEntity.getUid())
                .encryptionCode(userEntity.getEncryptionCode())
                .name(userEntity.getName())
                .dateOfBirth(userEntity.getDateOfBirth())
                .gender(Optional.ofNullable(userEntity.getGender()).orElse(UserEntity.Gender.기타))
                .loginType(Optional.ofNullable(userEntity.getLoginType()).orElse(UserEntity.LoginType.쇼셜))
                .userType(Optional.ofNullable(userEntity.getUserType()).orElse(UserEntity.UserType.개인))
                .phoneNumber(userEntity.getPhoneNumber())
                .job(userEntity.getJob())
                .region(userEntity.getRegion())
                .imgKey(userEntity.getImgKey())
                .category(userEntity.getCategory())
                .updateDate(userEntity.getUpdateDate())
                .createDate(userEntity.getCreateDate())
                .build();
    }
}
