package com.translationservice.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        jwtTokenProvider.secretKey = "verySecretKey12345678901234567890";
        jwtTokenProvider.validityInMilliseconds = 86400000;
        jwtTokenProvider.init();
    }

    @Test
    void createToken_ValidAuthentication_ReturnsToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("test", "test",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));

        String token = jwtTokenProvider.createToken(authentication);

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getAuthentication_ValidToken_ReturnsAuthentication() {
        Authentication originalAuth = new UsernamePasswordAuthenticationToken("test", "test",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        String token = jwtTokenProvider.createToken(originalAuth);

        Authentication resultAuth = jwtTokenProvider.getAuthentication(token);

        assertEquals(originalAuth.getName(), resultAuth.getName());
        assertEquals(originalAuth.getAuthorities(), resultAuth.getAuthorities());
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("test", "test",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        String token = jwtTokenProvider.createToken(authentication);

        boolean isValid = jwtTokenProvider.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        boolean isValid = jwtTokenProvider.validateToken("invalid_token");

        assertFalse(isValid);
    }

    @Test
    void getUsername_ValidToken_ReturnsUsername() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("test", "test", Collections.emptyList());
        String token = jwtTokenProvider.createToken(authentication);

        String username = jwtTokenProvider.getUsername(token);

        assertEquals("test", username);
    }
}
