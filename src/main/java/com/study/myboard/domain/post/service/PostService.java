package com.study.myboard.domain.post.service;

import com.study.myboard.domain.post.dto.PostRequestDto;
import com.study.myboard.domain.post.model.Post;
import com.study.myboard.domain.post.repository.PostRepository;
import com.study.myboard.domain.user.model.User;
import com.study.myboard.domain.user.repository.UserRepository;
import com.study.myboard.global.exception.CustomErrorCode;
import com.study.myboard.global.exception.CustomException;
import com.study.myboard.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * 게시글 등록
     */
    public void createPost(PostRequestDto request) {
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        Post post = request.toEntity(user);
        postRepository.save(post);
    }
}
