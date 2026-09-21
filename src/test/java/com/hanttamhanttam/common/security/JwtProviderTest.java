package com.hanttamhanttam.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    private static final String SECRET =
            "12345678901234567890123456789012";

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(
                SECRET,
                1800000L,
                1209600000L
        );
    }

    @Test
    void createAccessToken_success() {

        // given
        Long userId = 1L;

        // when
        String token = jwtProvider.createAccessToken(userId);

        // then
        assertNotNull(token);
        assertTrue(jwtProvider.validateToken(token));
        assertEquals(userId, jwtProvider.getUserId(token));
    }

    @Test
    void createRefreshToken_success() {

        // given
        Long userId = 1L;

        // when
        String token = jwtProvider.createRefreshToken(userId);

        // then
        assertNotNull(token);
        assertTrue(jwtProvider.validateToken(token));
        assertEquals(userId, jwtProvider.getUserId(token));
    }

    @Test
    void validateToken_tamperedToken_returnsFalse() {

        // given
        String token = jwtProvider.createAccessToken(1L);

        String tamperedToken =
                token.substring(0, token.length() - 1) + "x";

        // when
        boolean result = jwtProvider.validateToken(tamperedToken);

        // then
        assertFalse(result);
    }

    @Test
    void validateToken_expiredToken_returnsFalse()
            throws InterruptedException {

        // given
        JwtProvider shortLivedProvider =
                new JwtProvider(
                        SECRET,
                        1L,
                        1L
                );

        String token =
                shortLivedProvider.createAccessToken(1L);

        Thread.sleep(10);

        // when
        boolean result =
                shortLivedProvider.validateToken(token);

        // then
        assertFalse(result);
    }
}
