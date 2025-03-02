package com.translationservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String INVALID_TOKEN = "invalid.token";

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidToken_SetsAuthentication() throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer " + VALID_TOKEN);

        when(jwtTokenProvider.validateToken(VALID_TOKEN)).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(VALID_TOKEN)).thenReturn(authentication);


        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);


        verify(jwtTokenProvider).validateToken(VALID_TOKEN);
        verify(jwtTokenProvider).getAuthentication(VALID_TOKEN);
        assertSame(authentication, SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidToken_DoesNotSetAuthentication() throws ServletException, IOException {

        request.addHeader("Authorization", "Bearer " + INVALID_TOKEN);

        when(jwtTokenProvider.validateToken(INVALID_TOKEN)).thenReturn(false);


        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);


        verify(jwtTokenProvider).validateToken(INVALID_TOKEN);
        verify(jwtTokenProvider, never()).getAuthentication(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNoToken_DoesNotSetAuthentication() throws ServletException, IOException {

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);


        verify(jwtTokenProvider, never()).validateToken(anyString());
        verify(jwtTokenProvider, never()).getAuthentication(anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void resolveToken_WithValidBearerToken_ReturnsToken() throws Exception {

        request.addHeader("Authorization", "Bearer " + VALID_TOKEN);


        String token = invokeResolveToken(request);


        assertEquals(VALID_TOKEN, token);
    }

    @Test
    void resolveToken_WithNullHeader_ReturnsNull() throws Exception {

        String token = invokeResolveToken(request);


        assertNull(token);
    }

    @Test
    void resolveToken_WithEmptyHeader_ReturnsNull() throws Exception {

        request.addHeader("Authorization", "");


        String token = invokeResolveToken(request);


        assertNull(token);
    }

    @Test
    void resolveToken_WithNonBearerToken_ReturnsNull() throws Exception {

        request.addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==");


        String token = invokeResolveToken(request);


        assertNull(token);
    }

    @Test
    void resolveToken_WithBearerPrefixOnly_ReturnsEmptyString() throws Exception {

        request.addHeader("Authorization", "Bearer ");


        String token = invokeResolveToken(request);


        assertEquals("", token, "Token should be empty string with just 'Bearer ' prefix");
    }


    private String invokeResolveToken(HttpServletRequest request) throws Exception {
        Method resolveTokenMethod = JwtAuthenticationFilter.class.getDeclaredMethod("resolveToken", HttpServletRequest.class);
        resolveTokenMethod.setAccessible(true);
        return (String) resolveTokenMethod.invoke(jwtAuthenticationFilter, request);
    }
}