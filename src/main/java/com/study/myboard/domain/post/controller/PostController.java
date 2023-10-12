package com.study.myboard.domain.post.controller;

import com.study.myboard.domain.post.dto.PostRequestDto;
import com.study.myboard.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("")
    public ResponseEntity<String> createPost(@Valid @RequestBody PostRequestDto request){
        postService.createPost(request);
        return ResponseEntity.ok().body("게시글 등록 성공");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable("postId") Long postId){
        postService.deletePost(postId);
        return ResponseEntity.ok().body("게시글 삭제 성공");
    }
}
