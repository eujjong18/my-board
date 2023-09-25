package com.study.myboard.global.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // 인증번호 저장 ( key = Email / value = AuthCode )
    public void saveCode(String email, String code, Duration expirationDuration) {
        redisTemplate.opsForValue().set(email, code, expirationDuration);
    }

    public String getCode(String email) {
        String code = redisTemplate.opsForValue().get(email);
        if (code == null) {
            log.debug("[이메일 인증] RedisService.getcode() exception occur - email: {}, code: null");
            throw new NullPointerException("해당 이메일에 대한 인증번호 발송 내역이 존재하지 않습니다. 이메일을 확인해 주세요.");
        }
        return code;
    }

}
