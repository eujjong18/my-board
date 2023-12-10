package com.study.myboard.domain.user.service;

import com.study.myboard.domain.user.dto.TokenRefreshDto;
import com.study.myboard.domain.user.model.User;
import com.study.myboard.domain.user.repository.UserRepository;
import com.study.myboard.global.exception.CustomException;
import com.study.myboard.global.security.JwtTokenProvider;
import com.study.myboard.global.type.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Optional;

import static com.study.myboard.global.exception.CustomErrorCode.REFRESH_TOKEN_NOT_FOUND;
import static com.study.myboard.global.exception.CustomErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    // refresh token 업데이트 및 저장
    @Transactional
    public void persistRefreshToken(String refreshToken, String email){

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        user.updateRefreshToken(refreshToken);
    }


    // refresh token을 통해 access token 재발급
    public TokenRefreshDto refreshAccessToken(HttpServletRequest request){
        String refreshToken = jwtTokenProvider.resolveToken(request);
        String newAccessToken = null;

        // refresh token 검증
        User user = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(REFRESH_TOKEN_NOT_FOUND));

        boolean isValid = jwtTokenProvider.validateToken(refreshToken, TokenType.REFRESH_TOKEN);
        boolean isUserMatched = jwtTokenProvider.getUserPK(refreshToken).equals(user.getEmail());
        if(isValid && isUserMatched){
            // access token 재발급
            newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        }

        return new TokenRefreshDto(newAccessToken);
    }
}
