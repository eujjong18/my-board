package com.study.myboard.domain.user.model;

import com.study.myboard.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
}
