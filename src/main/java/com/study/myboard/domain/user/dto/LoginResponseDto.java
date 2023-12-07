package com.study.myboard.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public class LoginResponseDto {

    private String accessToken;
    private String refreshToken;

}
