package com.study.myboard.global.security;

import com.study.myboard.domain.user.model.User;
import com.study.myboard.domain.user.repository.UserRepository;
import com.study.myboard.domain.user.service.RefreshTokenService;
import com.study.myboard.global.exception.CustomException;
import com.study.myboard.global.type.Role;
import com.study.myboard.global.type.TokenType;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

import static com.study.myboard.global.exception.CustomErrorCode.*;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private String secretKey = "my-board-secret";

    // 토큰 유효시간
    private static final long ACCESS_TOKEN_VALID_TIME = 30 * 60 * 1000L; //30분
    private static final long REFRESH_TOKEN_VALID_TIME = 7 * 24 * 60 * 60 * 1000L; //1주일

    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;

    // 객체 초기화, secretKey를 Base64로 인코딩
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // access token 생성
    public String createAccessToken(String userPK, Role role) {
        Claims claims = Jwts.claims().setSubject(userPK);
        claims.put("role", role);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey) // 사용할 암호화 알고리즘과 signature에 들어갈 secret 값 세팅
                .compact();
    }

    // refresh token 생성
    public String createRefreshToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("role", user.getRole());
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // refresh token 업데이트 및 저장
        //refreshTokenService.persistRefreshToken(refreshToken, user.getEmail(), expiredAt);

        return refreshToken;
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        CustomUserDetails customUserDetails = customUserDetailsService.loadUserByUsername(this.getUserPK(token));
        return new UsernamePasswordAuthenticationToken(customUserDetails, "", customUserDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출 (email)
    public String getUserPK(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Request Header에서 토큰값 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken, TokenType type) {
        String tokenType = type.getName();

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException expiredJwtException) {
            // 토큰이 만료된 경우 처리
            throw new CustomException(TOKEN_EXPIRED, tokenType+": 토큰이 만료되었습니다.");
        } catch (MalformedJwtException malformedJwtException) {
            // 잘못된 형식의 토큰인 경우 처리
            throw new CustomException(INVALID_TOKEN_FORMAT, tokenType+": 잘못된 형식입니다.");
        } catch (Exception e) {
            // 그 외 다른 예외 처리
            //log.error("Token Validation Error 그 외: ", e.getMessage());
            throw new CustomException(TOKEN_VALIDATION_ERROR, tokenType+": 토큰 검증 중 오류가 발생했습니다.");
        }
    }

    // SecurityContextHolder 를 통해 userId를 가져옴
    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
            return user.getId();
        }
        return null;
    }

    // HttpServletRequest 를 통해 userId를 가져옴
    public Long getUserIdByServlet(HttpServletRequest request) {
        String token = resolveToken(request); //토큰 추출
        if (token != null && validateToken(token, TokenType.ACCESS_TOKEN)) {
            String userPk = getUserPK(token); //get email
            User user = userRepository.findByEmail(userPk)
                    .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
            return user.getId();
        }
        return null;
    }

}
