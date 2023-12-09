package com.study.myboard.global.security;

import com.study.myboard.domain.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
//@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    //private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // refresh token 삭제
        //refreshTokenRepository.deleteByEmail(authentication.getName());

        // 로그아웃 성공 시, 게시글 목록 조회 페이지로 리다이렉트
        response.sendRedirect("/board");
    }

}
