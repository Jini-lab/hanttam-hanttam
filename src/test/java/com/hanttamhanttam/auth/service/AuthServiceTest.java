package com.hanttamhanttam.auth.service;

import com.hanttamhanttam.auth.dto.SignupRequest;
import com.hanttamhanttam.auth.dto.SignupResponse;
import com.hanttamhanttam.user.domain.User;
import com.hanttamhanttam.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}
