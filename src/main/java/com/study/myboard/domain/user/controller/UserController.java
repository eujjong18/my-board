package com.study.myboard.domain.user.controller;

import com.study.myboard.domain.user.dto.UserRequestDto;
import com.study.myboard.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

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
    public ResponseEntity<String> signup(@RequestBody UserRequestDto.signupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok("회원가입 성공");
    }

}
