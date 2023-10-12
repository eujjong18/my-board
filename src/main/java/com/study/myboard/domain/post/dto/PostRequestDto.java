package com.study.myboard.domain.post.dto;

import com.study.myboard.domain.post.model.Post;
import com.study.myboard.domain.user.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Getter
@NoArgsConstructor
public class PostRequestDto {
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;

    public Post toEntity(User user){
        return Post.builder()
                .user(user)
                .title(Optional.ofNullable(title).orElseGet(() -> "제목없음"))
                .content(content)
                .view(0L)
                .build();
    }
}
