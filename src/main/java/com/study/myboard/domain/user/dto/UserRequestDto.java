package com.study.myboard.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UserRequestDto {

    // 이메일 인증코드 발송 요청
    @Getter
    @NoArgsConstructor
    public static class askCodeRequest {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;
    }

    @Getter
    @NoArgsConstructor
    // 이메일 인증코드 검증
    public static class verifyCodeRequest {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "인증번호는 필수 입력값입니다.")
        private String code;
    }


}
