package com.study.myboard.global.auth;

import com.study.myboard.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.study.myboard.global.exception.CustomErrorCode.VERIFICATION_CODE_NOT_FOUND;

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
            throw new CustomException(VERIFICATION_CODE_NOT_FOUND);
        }
        return code;
    }

}
