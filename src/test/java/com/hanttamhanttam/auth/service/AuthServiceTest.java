package com.hanttamhanttam.auth.service;

import com.hanttamhanttam.auth.domain.RefreshToken;
import com.hanttamhanttam.auth.dto.*;
import com.hanttamhanttam.auth.exception.InvalidCredentialsException;
import com.hanttamhanttam.auth.exception.InvalidRefreshTokenException;
import com.hanttamhanttam.auth.mapper.RefreshTokenMapper;
import com.hanttamhanttam.common.security.JwtProvider;
import com.hanttamhanttam.common.security.TokenHasher;
import com.hanttamhanttam.user.domain.User;
import com.hanttamhanttam.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private TokenHasher tokenHasher;

    @Mock
    private RefreshTokenMapper refreshTokenMapper;

    @InjectMocks
    private AuthService authService;


    @Test
    void signup_success() {

        // given
        SignupRequest request = new SignupRequest(
                "knitter@test.com",
                "password123!",
                "뜨개인"
        );

        when(userMapper.findByEmail("knitter@test.com"))
                .thenReturn(null);

        when(passwordEncoder.encode("password123!"))
                .thenReturn("encodedPassword");

        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1L);
            return null;
        }).when(userMapper).insert(any(User.class));


        // when
        SignupResponse response = authService.signup(request);


        // then
        assertEquals(1L, response.getUserId());
        assertEquals("knitter@test.com", response.getEmail());
        assertEquals("뜨개인", response.getNickname());

        verify(userMapper).findByEmail("knitter@test.com");
        verify(passwordEncoder).encode("password123!");
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void signup_duplicateEmail_throwsException() {

        // given
        SignupRequest request = new SignupRequest(
                "knitter@test.com",
                "password123!",
                "뜨개인"
        );

        User existingUser = new User();
        existingUser.setEmail("knitter@test.com");

        when(userMapper.findByEmail("knitter@test.com"))
                .thenReturn(existingUser);


        // when & then
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.signup(request)
                );

        assertEquals(
                "이미 사용 중인 이메일입니다.",
                exception.getMessage()
        );

        verify(userMapper, never()).insert(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void authenticate_success() {

        // given
        LoginRequest request = new LoginRequest(
                "knitter@test.com",
                "password123!"
        );

        User user = new User();
        user.setUserId(1L);
        user.setEmail("knitter@test.com");
        user.setPassword("encodedPassword");
        user.setNickname("뜨개인");

        when(userMapper.findByEmail("knitter@test.com"))
                .thenReturn(user);

        when(passwordEncoder.matches(
                "password123!",
                "encodedPassword"
        )).thenReturn(true);


        // when
        User authenticatedUser =
                authService.authenticate(request);


        // then
        assertEquals(1L, authenticatedUser.getUserId());
        assertEquals(
                "knitter@test.com",
                authenticatedUser.getEmail()
        );

        verify(passwordEncoder).matches(
                "password123!",
                "encodedPassword"
        );
    }

    @Test
    void authenticate_userNotFound_throwsException() {

        // given
        LoginRequest request = new LoginRequest(
                "unknown@test.com",
                "password123!"
        );

        when(userMapper.findByEmail("unknown@test.com"))
                .thenReturn(null);


        // when & then
        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.authenticate(request)
                );

        assertEquals(
                "이메일 또는 비밀번호가 올바르지 않습니다.",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .matches(anyString(), anyString());
    }

    @Test
    void authenticate_wrongPassword_throwsException() {

        // given
        LoginRequest request = new LoginRequest(
                "knitter@test.com",
                "wrongPassword"
        );

        User user = new User();
        user.setEmail("knitter@test.com");
        user.setPassword("encodedPassword");

        when(userMapper.findByEmail("knitter@test.com"))
                .thenReturn(user);

        when(passwordEncoder.matches(
                "wrongPassword",
                "encodedPassword"
        )).thenReturn(false);


        // when & then
        InvalidCredentialsException exception =
                assertThrows(
                        InvalidCredentialsException.class,
                        () -> authService.authenticate(request)
                );

        assertEquals(
                "이메일 또는 비밀번호가 올바르지 않습니다.",
                exception.getMessage()
        );
    }

    @Test
    void login_success() {

        // given
        LoginRequest request = new LoginRequest(
                "knitter@test.com",
                "password123!"
        );

        User user = new User();
        user.setUserId(1L);
        user.setEmail("knitter@test.com");
        user.setPassword("encodedPassword");

        when(userMapper.findByEmail("knitter@test.com"))
                .thenReturn(user);

        when(passwordEncoder.matches(
                "password123!",
                "encodedPassword"
        )).thenReturn(true);

        when(jwtProvider.createAccessToken(1L))
                .thenReturn("access-token");

        when(jwtProvider.createRefreshToken(1L))
                .thenReturn("refresh-token");

        when(tokenHasher.hash("refresh-token"))
                .thenReturn("hashed-refresh-token");

        LocalDateTime expiresAt =
                LocalDateTime.now().plusDays(14);

        when(jwtProvider.getRefreshTokenExpiresAt())
                .thenReturn(expiresAt);


        // when
        LoginResult result =
                authService.login(request);


        // then
        assertEquals(
                "access-token",
                result.getAccessToken()
        );

        assertEquals(
                "refresh-token",
                result.getRefreshToken()
        );

        ArgumentCaptor<RefreshToken> captor =
                ArgumentCaptor.forClass(RefreshToken.class);

        verify(refreshTokenMapper)
                .upsert(captor.capture());

        RefreshToken savedToken = captor.getValue();

        assertEquals(1L, savedToken.getUserId());

        assertEquals(
                "hashed-refresh-token",
                savedToken.getTokenHash()
        );

        assertEquals(
                expiresAt,
                savedToken.getExpiresAt()
        );
    }

    @Test
    void refresh_success() {

        // given
        String refreshToken = "refresh-token";

        RefreshToken savedToken = new RefreshToken();
        savedToken.setUserId(1L);
        savedToken.setTokenHash("hashed-refresh-token");

        when(jwtProvider.validateToken(refreshToken))
                .thenReturn(true);

        when(jwtProvider.isRefreshToken(refreshToken))
                .thenReturn(true);

        when(jwtProvider.getUserId(refreshToken))
                .thenReturn(1L);

        when(refreshTokenMapper.findByUserId(1L))
                .thenReturn(savedToken);

        when(tokenHasher.hash(refreshToken))
                .thenReturn("hashed-refresh-token");

        when(jwtProvider.createAccessToken(1L))
                .thenReturn("new-access-token");


        // when
        String accessToken =
                authService.refresh(refreshToken);


        // then
        assertEquals(
                "new-access-token",
                accessToken
        );

        verify(jwtProvider)
                .createAccessToken(1L);
    }

    @Test
    void refresh_hashMismatch_throwsException() {

        // given
        String refreshToken = "old-refresh-token";

        RefreshToken savedToken = new RefreshToken();
        savedToken.setUserId(1L);
        savedToken.setTokenHash("new-token-hash");

        when(jwtProvider.validateToken(refreshToken))
                .thenReturn(true);

        when(jwtProvider.isRefreshToken(refreshToken))
                .thenReturn(true);

        when(jwtProvider.getUserId(refreshToken))
                .thenReturn(1L);

        when(refreshTokenMapper.findByUserId(1L))
                .thenReturn(savedToken);

        when(tokenHasher.hash(refreshToken))
                .thenReturn("old-token-hash");


        // when & then
        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authService.refresh(refreshToken)
        );

        verify(jwtProvider, never())
                .createAccessToken(anyLong());
    }

    @Test
    void refresh_invalidToken_throwsException() {

        // given
        String refreshToken = "invalid-token";

        when(jwtProvider.validateToken(refreshToken))
                .thenReturn(false);


        // when & then
        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authService.refresh(refreshToken)
        );

        verify(refreshTokenMapper, never())
                .findByUserId(anyLong());

        verify(jwtProvider, never())
                .createAccessToken(anyLong());
    }

    @Test
    void logout_success() {

        // given
        String refreshToken = "refresh-token";

        RefreshToken savedToken = new RefreshToken();
        savedToken.setUserId(1L);
        savedToken.setTokenHash("hashed-refresh-token");

        when(jwtProvider.validateToken(refreshToken))
                .thenReturn(true);

        when(jwtProvider.isRefreshToken(refreshToken))
                .thenReturn(true);

        when(jwtProvider.getUserId(refreshToken))
                .thenReturn(1L);

        when(refreshTokenMapper.findByUserId(1L))
                .thenReturn(savedToken);

        when(tokenHasher.hash(refreshToken))
                .thenReturn("hashed-refresh-token");


        // when
        authService.logout(refreshToken);


        // then
        verify(refreshTokenMapper)
                .deleteByUserId(1L);
    }

    @Test
    void logout_invalidToken_throwsException() {

        // given
        String refreshToken = "invalid-token";

        when(jwtProvider.validateToken(refreshToken))
                .thenReturn(false);


        // when & then
        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authService.logout(refreshToken)
        );

        verify(refreshTokenMapper, never())
                .deleteByUserId(anyLong());
    }

    @Test
    void logout_hashMismatch_throwsException() {

        // given
        String refreshToken = "old-refresh-token";

        RefreshToken savedToken = new RefreshToken();
        savedToken.setUserId(1L);
        savedToken.setTokenHash("current-token-hash");

        when(jwtProvider.validateToken(refreshToken))
                .thenReturn(true);

        when(jwtProvider.isRefreshToken(refreshToken))
                .thenReturn(true);

        when(jwtProvider.getUserId(refreshToken))
                .thenReturn(1L);

        when(refreshTokenMapper.findByUserId(1L))
                .thenReturn(savedToken);

        when(tokenHasher.hash(refreshToken))
                .thenReturn("old-token-hash");


        // when & then
        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authService.logout(refreshToken)
        );

        verify(refreshTokenMapper, never())
                .deleteByUserId(anyLong());
    }

    @Test
    void logout_accessToken_throwsException() {

        // given
        String accessToken = "access-token";

        when(jwtProvider.validateToken(accessToken))
                .thenReturn(true);

        when(jwtProvider.isRefreshToken(accessToken))
                .thenReturn(false);

        // when & then
        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authService.logout(accessToken)
        );

        verify(refreshTokenMapper, never())
                .deleteByUserId(anyLong());
    }

    @Test
    void me_success() {

        // given
        User user = new User();
        user.setUserId(1L);
        user.setEmail("knitter@test.com");
        user.setNickname("뜨개인");

        when(userMapper.findById(1L))
                .thenReturn(user);


        // when
        MeResponse response =
                authService.me(1L);


        // then
        assertEquals(
                1L,
                response.getUserId()
        );

        assertEquals(
                "knitter@test.com",
                response.getEmail()
        );

        assertEquals(
                "뜨개인",
                response.getNickname()
        );
    }

    @Test
    void refresh_accessToken_throwsException() {

        // given
        String accessToken = "access-token";

        when(jwtProvider.validateToken(accessToken))
                .thenReturn(true);

        when(jwtProvider.isRefreshToken(accessToken))
                .thenReturn(false);

        // when & then
        assertThrows(
                InvalidRefreshTokenException.class,
                () -> authService.refresh(accessToken)
        );

        verify(refreshTokenMapper, never())
                .findByUserId(anyLong());
    }
}
