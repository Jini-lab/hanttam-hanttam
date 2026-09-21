package com.hanttamhanttam.auth.controller;

import com.hanttamhanttam.auth.dto.LoginRequest;
import com.hanttamhanttam.auth.dto.LoginResult;
import com.hanttamhanttam.auth.dto.SignupRequest;
import com.hanttamhanttam.auth.dto.SignupResponse;
import com.hanttamhanttam.auth.exception.InvalidCredentialsException;
import com.hanttamhanttam.auth.exception.InvalidRefreshTokenException;
import com.hanttamhanttam.auth.service.AuthService;
import com.hanttamhanttam.common.exception.GlobalExceptionHandler;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import com.hanttamhanttam.common.config.SecurityConfig;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// HTTP / JSON / Validation 검증
@WebMvcTest(AuthController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class
})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void signup_success() throws Exception {

        // given
        SignupRequest request = new SignupRequest(
                "knitter@test.com",
                "password123!",
                "뜨개인"
        );

        SignupResponse response = new SignupResponse(
                1L,
                "knitter@test.com",
                "뜨개인"
        );

        when(authService.signup(any(SignupRequest.class)))
                .thenReturn(response);


        // when & then
        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.email").value("knitter@test.com"))
                .andExpect(jsonPath("$.nickname").value("뜨개인"));
    }
    @Test
    void signup_invalidEmail_returns400() throws Exception {

        SignupRequest request = new SignupRequest(
                "not-email",
                "password123!",
                "뜨개인"
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void signup_shortPassword_returns400() throws Exception {

        SignupRequest request = new SignupRequest(
                "knitter@test.com",
                "1234",
                "뜨개인"
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    @Test
    void signup_blankNickname_returns400() throws Exception {

        SignupRequest request = new SignupRequest(
                "knitter@test.com",
                "password123!",
                ""
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_success() throws Exception {

        // given
        LoginRequest request =
                new LoginRequest(
                        "knitter@test.com",
                        "password123!"
                );

        LoginResult result =
                new LoginResult(
                        "access-token",
                        "refresh-token"
                );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(result);


        // when & then
        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.accessToken")
                                .value("access-token")
                )
                .andExpect(
                        header().string(
                                HttpHeaders.SET_COOKIE,
                                org.hamcrest.Matchers.containsString(
                                        "refreshToken=refresh-token"
                                )
                        )
                )
                .andExpect(
                        header().string(
                                HttpHeaders.SET_COOKIE,
                                org.hamcrest.Matchers.containsString(
                                        "HttpOnly"
                                )
                        )
                )
                .andExpect(
                        jsonPath("$.refreshToken").doesNotExist()
                );
    }

    @Test
    void login_invalidEmail_returns400() throws Exception {

        LoginRequest request =
                new LoginRequest(
                        "invalid-email",
                        "password123!"
                );

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_wrongPassword_returns401()
            throws Exception {

        LoginRequest request =
                new LoginRequest(
                        "knitter@test.com",
                        "wrongPassword"
                );

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(
                        new InvalidCredentialsException()
                );

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isUnauthorized())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "이메일 또는 비밀번호가 올바르지 않습니다."
                                )
                );
    }

    @Test
    void refresh_success() throws Exception {

        // given
        when(authService.refresh("refresh-token"))
                .thenReturn("new-access-token");

        // when & then
        mockMvc.perform(
                        post("/api/auth/refresh")
                                .cookie(
                                        new Cookie(
                                                "refreshToken",
                                                "refresh-token"
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.accessToken")
                                .value("new-access-token")
                );

        verify(authService)
                .refresh("refresh-token");
    }

    @Test
    void refresh_withoutCookie_returns401()
            throws Exception {

        mockMvc.perform(
                        post("/api/auth/refresh")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "유효하지 않은 Refresh Token입니다."
                                )
                );

        verify(authService, never())
                .refresh(anyString());
    }

    @Test
    void refresh_invalidToken_returns401()
            throws Exception {

        when(authService.refresh("invalid-refresh-token"))
                .thenThrow(
                        new InvalidRefreshTokenException()
                );

        mockMvc.perform(
                        post("/api/auth/refresh")
                                .cookie(
                                        new Cookie(
                                                "refreshToken",
                                                "invalid-refresh-token"
                                        )
                                )
                )
                .andExpect(status().isUnauthorized())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "유효하지 않은 Refresh Token입니다."
                                )
                );
    }
}
