package com.study.myboard.domain.user.model;

import com.study.myboard.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Date expiredAt;


    // refresh token 업데이트
    public void update(String refreshToken){
        this.refreshToken = refreshToken;
    }

}
