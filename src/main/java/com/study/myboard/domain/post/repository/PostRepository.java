package com.study.myboard.domain.post.repository;

import com.study.myboard.domain.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
