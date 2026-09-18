package com.hanttamhanttam.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor //테스트에서 편하게 생성
public class SignupRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min=8, max=100)
    private String password;

    @NotBlank
    @Size(max = 50)
    private String nickname;
}
