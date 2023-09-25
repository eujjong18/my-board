package com.study.myboard.domain.user.service;

import com.study.myboard.domain.user.dto.UserRequestDto;
import com.study.myboard.global.auth.MailService;
import com.study.myboard.global.auth.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

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
            throw new NoSuchAlgorithmException("** UserService.createCode() exception occur!");
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
            log.debug("[이메일 인증] UserService.verifyCode() exception occur - requestCode: {}, redisCode: {}", request.getCode(), redisAuthCode);
            throw new RuntimeException("인증번호가 일치하지 않습니다.");
        }
    }


}
