package com.study.myboard.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.myboard.domain.user.model.User;
import com.study.myboard.global.exception.CustomErrorCode;
import com.study.myboard.global.exception.CustomException;
import com.study.myboard.global.exception.ErrorResponse;
import com.study.myboard.global.type.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 헤더에서 JWT를 받아옴
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);

        // 유효한 토큰인지 확인
        try{
            if (token != null && jwtTokenProvider.validateToken(token, TokenType.ACCESS_TOKEN)) {
                // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옴
                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                // SecurityContext에 Authentication 객체를 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (CustomException customException){
            CustomErrorCode code = customException.getCustomErrorCode();

            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(code.getCode())
                    .message(customException.getMessage())
                    .build();

            HttpServletResponse res = (HttpServletResponse) response;
            res.setContentType("application/json;charset=UTF-8");
            res.setStatus(code.getHttpStatus().value());
            res.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return;
        }

        chain.doFilter(request, response);
    }
}
