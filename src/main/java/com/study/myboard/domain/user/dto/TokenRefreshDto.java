package com.study.myboard.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * refresh token을 통해 access token 재발급
 */
@Getter
@AllArgsConstructor
public class TokenRefreshDto {
    private String accessToken;
}
