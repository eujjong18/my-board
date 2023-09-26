package com.study.myboard.domain.user.service;

import com.study.myboard.domain.user.dto.UserRequestDto;
import com.study.myboard.domain.user.model.User;
import com.study.myboard.domain.user.repository.UserRepository;
import com.study.myboard.global.auth.MailService;
import com.study.myboard.global.auth.RedisService;
import com.study.myboard.global.exception.CustomErrorCode;
import com.study.myboard.global.exception.CustomException;
import com.study.myboard.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

import static com.study.myboard.global.exception.CustomErrorCode.*;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final MailService mailService;
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;


    /**
     * 이메일 인증
     */
    // 랜덤 인증번호 생성
    private String createCode() throws NoSuchAlgorithmException {
        int lenth = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lenth; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(VERIFICATION_CODE_GENERATION_ERROR);
        }
    }

    // 인증번호 발송
    public void sendCodeToEmail(UserRequestDto.askCodeRequest request) throws NoSuchAlgorithmException {
        // 이메일 가입 여부 확인
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new CustomException(EMAIL_ALREADY_EXISTS);
        });

        String title = "my-board 이메일 인증 번호";
        String authCode = createCode();
        mailService.sendEmail(request.getEmail(), title, authCode);

        // 인증번호 Redis에 저장 ( key = Email / value = AuthCode )
        redisService.saveCode(request.getEmail(), authCode, Duration.ofMillis(this.authCodeExpirationMillis));
    }

    // 인증번호 검증
    public void verifyCode(UserRequestDto.verifyCodeRequest request) {
        String redisAuthCode = redisService.getCode(request.getEmail());
        boolean authResult = redisAuthCode.equals(request.getCode());
        if(authResult == false){
            throw new CustomException(VERIFICATION_CODE_MISMATCH);
        }
    }

    /**
     * 회원가입
     */
    public void signup(UserRequestDto.signupRequest request){
        // 닉네임 중복 여부 확인
        userRepository.findByNickname(request.getNickname()).ifPresent(user -> {
            throw new CustomException(NICKNAME_ALREADY_EXISTS);
        });

        // 이메일 가입 여부 확인
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new CustomException(EMAIL_ALREADY_EXISTS);
        });

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        // 유저 등록
        User newUser = request.toEntity(encryptedPassword);
        userRepository.save(newUser);
    }

    /**
     * 로그인
     */
    public String login(UserRequestDto.loginRequest request){
        // 이메일 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(LOGIN_FAILED);
        }

        // 토큰 생성 후 반환
        return jwtTokenProvider.createToken(user.getEmail(), user.getRole());
    }

}
