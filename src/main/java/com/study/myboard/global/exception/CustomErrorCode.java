package com.study.myboard.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum CustomErrorCode {

    // Common (1xxx)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 1001, "서버 내부에 오류가 있습니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, 1002, "잘못된 입력값입니다."),

    // User (2xxx)
    VERIFICATION_CODE_GENERATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2001, "이메일 인증번호 생성 오류가 발생했습니다."),
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2002, "이메일 인증번호 발송 오류가 발생했습니다."),
    VERIFICATION_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, 2003, "이메일 인증번호 검증 오류가 발생했습니다."),
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, 2004, "인증번호가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
