package com.study.myboard.domain.post.service;

import com.study.myboard.domain.post.dto.PostRequestDto;
import com.study.myboard.domain.post.model.Post;
import com.study.myboard.domain.post.repository.PostRepository;
import com.study.myboard.domain.user.model.User;
import com.study.myboard.domain.user.repository.UserRepository;
import com.study.myboard.global.exception.CustomException;
import com.study.myboard.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.study.myboard.global.exception.CustomErrorCode.*;


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
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Post post = request.toEntity(user);
        postRepository.save(post);
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public void updatePost(Long postId, PostRequestDto request) {
        // 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        // 게시글 수정 권한 확인
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if(post.getUser() != user){
            throw new CustomException(NO_PERMISSION, "해당 게시글에 대한 수정 권한이 없습니다.");
        }

        // 게시글 수정
        post.update(request);
    }

    /**
     * 게시글 삭제
     */
    public void deletePost(Long postId) {
        // 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(POST_NOT_FOUND));

        // 게시글 삭제 권한 확인
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if(post.getUser() != user){
            throw new CustomException(NO_PERMISSION, "해당 게시글에 대한 삭제 권한이 없습니다.");
        }

        // 게시글 삭제
        postRepository.delete(post);
    }

}
