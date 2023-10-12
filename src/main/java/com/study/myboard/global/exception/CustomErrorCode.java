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
    VERIFICATION_CODE_MISMATCH(HttpStatus.BAD_REQUEST, 2004, "인증번호가 일치하지 않습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, 2005, "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, 2006, "이미 존재하는 닉네임입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 2007, "사용자를 찾을 수 없습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, 2008, "토큰이 만료되었습니다."),
    INVALID_TOKEN_FORMAT(HttpStatus.BAD_REQUEST, 2009, "잘못된 토큰 형식입니다."),
    TOKEN_VALIDATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 2010, "토큰 검증 중 오류가 발생했습니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, 2011, "로그인 오류 : 아이디 또는 비밀번호가 일치하지 않습니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, 2012, "게시글 수정 및 삭제 권한이 없습니다."),

    // Post (3xxx)
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, 3001, "존재하지 않는 게시글입니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
