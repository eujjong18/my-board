package com.study.myboard.domain.user.service;

import com.study.myboard.domain.user.dto.UserRequestDto;
import com.study.myboard.global.auth.MailService;
import com.study.myboard.global.auth.RedisService;
import com.study.myboard.global.exception.CustomErrorCode;
import com.study.myboard.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

import static com.study.myboard.global.exception.CustomErrorCode.VERIFICATION_CODE_GENERATION_ERROR;
import static com.study.myboard.global.exception.CustomErrorCode.VERIFICATION_CODE_MISMATCH;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final MailService mailService;
    private final RedisService redisService;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;


    /**
     * 이메일 인증
     */
    // 랜덤 인증코드 생성
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

    // 인증코드 발송
    public void sendCodeToEmail(UserRequestDto.askCodeRequest request) throws NoSuchAlgorithmException {
        String title = "my-board 이메일 인증 번호";
        String authCode = createCode();
        mailService.sendEmail(request.getEmail(), title, authCode);

        //인증 번호 Redis에 저장 ( key = Email / value = AuthCode )
        redisService.saveCode(request.getEmail(), authCode, Duration.ofMillis(this.authCodeExpirationMillis));
    }

    // 인증코드 검증
    public void verifyCode(UserRequestDto.verifyCodeRequest request) {
        String redisAuthCode = redisService.getCode(request.getEmail());
        boolean authResult = redisAuthCode.equals(request.getCode());

        if(authResult == false){
            throw new CustomException(VERIFICATION_CODE_MISMATCH);
        }
    }


}
