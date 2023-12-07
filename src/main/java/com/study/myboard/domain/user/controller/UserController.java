package com.study.myboard.domain.user.controller;

import com.study.myboard.domain.user.dto.LoginResponseDto;
import com.study.myboard.domain.user.dto.TokenRefreshDto;
import com.study.myboard.domain.user.dto.UserRequestDto;
import com.study.myboard.domain.user.service.RefreshTokenService;
import com.study.myboard.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    // 인증번호 발송 요청
    @PostMapping("/email/code-request")
    public ResponseEntity<String> requestVerificationCode(@Valid @RequestBody UserRequestDto.askCodeRequest request)
            throws NoSuchAlgorithmException {
        userService.sendCodeToEmail(request);
        return ResponseEntity.ok("인증번호 발송 성공");
    }

    // 인증번호 검증
    @PostMapping("/email/code-verification")
    public ResponseEntity<String> verifyVerificationCode(@Valid @RequestBody UserRequestDto.verifyCodeRequest request) {
        userService.verifyCode(request);
        return ResponseEntity.ok("인증번호 확인 성공");
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserRequestDto.signupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody UserRequestDto.loginRequest request){
        LoginResponseDto allToken = userService.login(request);
        return ResponseEntity.ok(allToken);
    }

    // access token 재발급
    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshDto> refreshAccessToken(HttpServletRequest request){
        // refresh token 검증
        // 유효하다면, access token만 재발급 (refresh token은 그대로 유지)
        // 유효하지 않다면, refresh token 만료 응답
        TokenRefreshDto newAccessToken = refreshTokenService.refreshAccessToken(request);
        return ResponseEntity.ok(newAccessToken);
    }

}
