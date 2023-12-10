package com.study.myboard.global.security;

import com.study.myboard.domain.user.model.User;
import com.study.myboard.domain.user.repository.UserRepository;
import com.study.myboard.global.exception.CustomErrorCode;
import com.study.myboard.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 사용자 정보 얻기
        User authUser = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        User user = userRepository.findByEmail(authUser.getEmail())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // refresh token null로 변경
        user.updateRefreshToken(null);

        // 로그아웃 성공 시, 게시글 목록 조회 페이지로 리다이렉트
        response.sendRedirect("/board");
    }

}
