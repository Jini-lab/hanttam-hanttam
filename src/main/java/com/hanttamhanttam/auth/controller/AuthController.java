package com.hanttamhanttam.auth.controller;

import com.hanttamhanttam.auth.dto.*;
import com.hanttamhanttam.auth.exception.InvalidRefreshTokenException;
import com.hanttamhanttam.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SignupRequest request
            ) {
        SignupResponse response = authService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {

        LoginResult result =
                authService.login(request);

        ResponseCookie refreshTokenCookie =
                ResponseCookie.from(
                                "refreshToken",
                                result.getRefreshToken()
                        )
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Lax")
                        .path("/api/auth")
                        .maxAge(Duration.ofDays(14))
                        .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                refreshTokenCookie.toString()
        );

        LoginResponse loginResponse =
                new LoginResponse(
                        result.getAccessToken()
                );

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refresh(
            @CookieValue(
                    value = "refreshToken",
                    required = false
            ) String refreshToken
    ) {

        if (refreshToken == null) {
            throw new InvalidRefreshTokenException();
        }

        String accessToken =
                authService.refresh(refreshToken);

        return ResponseEntity.ok(
                new TokenRefreshResponse(accessToken)
        );
    }
}
