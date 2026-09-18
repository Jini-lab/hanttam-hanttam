package com.hanttamhanttam.auth.controller;

import com.hanttamhanttam.auth.dto.SignupRequest;
import com.hanttamhanttam.auth.dto.SignupResponse;
import com.hanttamhanttam.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import com.hanttamhanttam.common.config.SecurityConfig;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// HTTP / JSON / Validation 검증
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
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

}
