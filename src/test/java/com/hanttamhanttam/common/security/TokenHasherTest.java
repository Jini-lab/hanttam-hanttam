package com.hanttamhanttam.common.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenHasherTest {

    private final TokenHasher tokenHasher = new TokenHasher();

    @Test
    void hash_success() {
        // given
        String token = "refresh-token";

        // when
        String hash = tokenHasher.hash(token);

        // then
        assertNotNull(hash);
        assertEquals(64, hash.length());
        assertNotEquals(token, hash);
    }

    @Test
    void sameToken_producesSameHash() {

        // given
        String token = "refresh-token";

        // when
        String hash1 = tokenHasher.hash(token);
        String hash2 = tokenHasher.hash(token);

        // then
        assertEquals(hash1, hash2);
    }

    @Test
    void differentTokens_produceDifferentHashes() {

        // when
        String hash1 = tokenHasher.hash("token-A");
        String hash2 = tokenHasher.hash("token-B");

        // then
        assertNotEquals(hash1, hash2);
    }
}
