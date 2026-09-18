package com.hanttamhanttam.user.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class User {

    private Long userId;
    private String email;
    private String password;
    private String nickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
