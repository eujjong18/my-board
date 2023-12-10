package com.study.myboard.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final CustomErrorCode customErrorCode;
    private final String message;


    public CustomException(CustomErrorCode customErrorCode){
        super(customErrorCode.getMessage());
        this.customErrorCode = customErrorCode;
        this.message = customErrorCode.getMessage();
    }

    public CustomException(CustomErrorCode customErrorCode, String message){
        super(message);
        this.customErrorCode = customErrorCode;
        this.message = message;
    }
}
