package com.hanttamhanttam.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void noAuthorizationHeader_doesNotAuthenticate()
            throws Exception {

        // given
        when(request.getHeader("Authorization"))
                .thenReturn(null);

        // when
        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        // then
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNull(authentication);

        verifyNoInteractions(jwtProvider);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void validToken_setsAuthentication()
            throws Exception {

        // given
        String token = "valid-access-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtProvider.validateToken(token))
                .thenReturn(true);

        when(jwtProvider.isAccessToken(token))
                .thenReturn(true);

        when(jwtProvider.getUserId(token))
                .thenReturn(1L);

        // when
        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        // then
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNotNull(authentication);
        assertEquals(
                1L,
                authentication.getPrincipal()
        );

        verify(jwtProvider)
                .validateToken(token);

        verify(jwtProvider)
                .getUserId(token);

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void invalidToken_doesNotAuthenticate()
            throws Exception {

        // given
        String token = "invalid-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtProvider.validateToken(token))
                .thenReturn(false);

        // when
        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        // then
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNull(authentication);

        verify(jwtProvider, never())
                .getUserId(anyString());

        verify(filterChain)
                .doFilter(request, response);
    }

    @Test
    void refreshToken_doesNotAuthenticate()
            throws Exception {

        // given
        String token = "refresh-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtProvider.validateToken(token))
                .thenReturn(true);

        when(jwtProvider.isAccessToken(token))
                .thenReturn(false);

        // when
        jwtAuthenticationFilter.doFilter(
                request,
                response,
                filterChain
        );

        // then
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assertNull(authentication);

        verify(jwtProvider)
                .validateToken(token);

        verify(jwtProvider)
                .isAccessToken(token);

        verify(jwtProvider, never())
                .getUserId(anyString());

        verify(filterChain)
                .doFilter(request, response);
    }
}
