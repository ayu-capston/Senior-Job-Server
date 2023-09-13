package com.seniorjob.seniorjobserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seniorjob.seniorjobserver.domain.entity.UserEntity;
import com.seniorjob.seniorjobserver.dto.UserDto;
import com.seniorjob.seniorjobserver.repository.UserRepository;
import com.seniorjob.seniorjobserver.service.StorageService;
import com.seniorjob.seniorjobserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository, PasswordEncoder pwEncoder, StorageService storageService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.objectMapper = objectMapper;
    }

    // 구직자/사업주 회원가입API with 암호화 세션
    @PostMapping("/join")
    public ResponseEntity<?> registerUser(@RequestParam("userDto") String userDtoJson, @RequestParam("file") MultipartFile file) {
        try {
            UserDto userDto = objectMapper.readValue(userDtoJson, UserDto.class);

            String encryptionCode = userDto.getEncryptionCode();
            String imageUrl = storageService.uploadImage(file);
            userDto.setImgKey(imageUrl);

            UserEntity userEntity = userService.createUser(userDto);
            return ResponseEntity.ok(userEntity);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image: " + e.getMessage());
        }
    }
    // 세션 로그인
    // POST /api/users/login
    @PostMapping("/login")
    public ResponseEntity<?> loginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();

        UserEntity user = userRepository.findByPhoneNumber(currentUserName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone number: " + currentUserName));

        return ResponseEntity.ok(user.getName() + " 회원님 로그인에 성공하였습니다");
    }

    // 세션 로그아웃
    // POST /api/users/logout

    // 회원전체목록API
    // GET /api/users/all
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserDto> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT); // 회원이 없을 때의 메시지를 반환합니다.
        } catch (Exception e) {
            // 로깅 추가
            e.printStackTrace(); // 이것은 임시 로깅입니다. 실제 프로덕션에서는 SLF4J, Logback 등의 로깅 프레임워크를 사용하여 로그를 남기는 것이 좋습니다.
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 그 외의 에러 메시지를 반환합니다.
        }
    }

    // 로그인된 회원정보api
    // GET /api/users/detail
    @GetMapping("/detail")
    public ResponseEntity<?> getUserDetails() {
        try {
            UserDto userDto = userService.getLoggedInUserDetails();
            return ResponseEntity.ok(userDto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("에러 : " + e.getMessage());
        }
    }

    // 회원정보수정API
    // PUT /api/users/update/(현재로그인된 user의 정보를 불러와 수정)
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserDto userDto){
        try {
            UserDto updateUser = userService.updateUser(userDto);
            return ResponseEntity.ok(updateUser);
        }catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }catch (UsernameNotFoundException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("에러 : " + e.getMessage());
        }
    }
}
