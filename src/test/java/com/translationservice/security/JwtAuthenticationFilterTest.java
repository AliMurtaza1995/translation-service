package com.translationservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Test
    void doFilterInternal_NoToken_ContinuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtTokenProvider, never()).validateToken(anyString());
        verify(jwtTokenProvider, never()).getAuthentication(anyString());
    }

    @Test
    void doFilterInternal_InvalidToken_ContinuesChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid_token");
        when(jwtTokenProvider.validateToken("invalid_token")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtTokenProvider, times(1)).validateToken("invalid_token");
        verify(jwtTokenProvider, never()).getAuthentication(anyString());
    }

    @Test
    void doFilterInternal_ValidToken_SetsAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid_token");
        when(jwtTokenProvider.validateToken("valid_token")).thenReturn(true);
        Authentication auth = new UsernamePasswordAuthenticationToken("test", "test",
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
        when(jwtTokenProvider.getAuthentication("valid_token")).thenReturn(auth);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtTokenProvider, times(1)).validateToken("valid_token");
        verify(jwtTokenProvider, times(1)).getAuthentication("valid_token");
    }
}