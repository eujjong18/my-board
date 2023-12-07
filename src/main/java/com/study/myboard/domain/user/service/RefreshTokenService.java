package com.study.myboard.domain.user.service;

import com.study.myboard.domain.user.dto.TokenRefreshDto;
import com.study.myboard.domain.user.model.RefreshToken;
import com.study.myboard.domain.user.model.User;
import com.study.myboard.domain.user.repository.RefreshTokenRepository;
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

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    // refresh token 업데이트 및 저장
    @Transactional
    public void persistRefreshToken(String refreshToken, String email){

        Optional<RefreshToken> findRefreshToken = refreshTokenRepository.findByEmail(email);

        // refresh token이 이미 있다면 업데이트, 없다면 저장
        if(findRefreshToken.isPresent()){
            findRefreshToken.get().update(refreshToken);
        }else{
            Date expiredAt = jwtTokenProvider.getTokenExpirationDate(refreshToken);
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .refreshToken(refreshToken)
                    .email(email)
                    .expiredAt(expiredAt)
                    .build();
            refreshTokenRepository.save(newRefreshToken);
        }
    }


    // refresh token을 통해 access token 재발급
    public TokenRefreshDto refreshAccessToken(HttpServletRequest request){
        String refreshToken = jwtTokenProvider.resolveToken(request);
        String newAccessToken = null;

        // refresh token 검증
        RefreshToken findRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(REFRESH_TOKEN_NOT_FOUND));
        if(jwtTokenProvider.validateToken(refreshToken, TokenType.REFRESH_TOKEN)){
            // access token 재발급
            User user = userRepository.findByEmail(findRefreshToken.getEmail()).get();
            newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        }

        return new TokenRefreshDto(newAccessToken);
    }
}
