package com.translationservice.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    private final String TEST_TOKEN = "test-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(securityConfig, "securityToken", TEST_TOKEN);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testUserDetailsService() {
        // Act
        UserDetailsService userDetailsService = securityConfig.userDetailsService();
        UserDetails userDetails = userDetailsService.loadUserByUsername("user");

        // Assert
        assertNotNull(userDetailsService);
        assertNotNull(userDetails);
        assertEquals("user", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    // Testing the token filter with Swagger UI path
    @Test
    void testTokenFilter_SwaggerUiPath() throws Exception {
        // Use a fresh instance of SecurityConfig to avoid interference
        SecurityConfig configForTest = new SecurityConfig();
        ReflectionTestUtils.setField(configForTest, "securityToken", TEST_TOKEN);

        // Create a fresh request and response
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/swagger-ui/index.html");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Get a filter from our fresh config instance
        OncePerRequestFilter filter = configForTest.tokenAuthenticationFilter();

        // Clear security context to ensure test isolation
        SecurityContextHolder.clearContext();

        // Act
        invokeDoFilterInternal(filter, request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Authentication should be null for Swagger UI path");
    }

    @Test
    void testTokenFilter_ApiDocsPath() throws Exception {
        // Use a fresh instance of SecurityConfig to avoid interference
        SecurityConfig configForTest = new SecurityConfig();
        ReflectionTestUtils.setField(configForTest, "securityToken", TEST_TOKEN);

        // Create a fresh request and response
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/v3/api-docs/swagger-config");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Get a filter from our fresh config instance
        OncePerRequestFilter filter = configForTest.tokenAuthenticationFilter();

        // Clear security context to ensure test isolation
        SecurityContextHolder.clearContext();

        // Act
        invokeDoFilterInternal(filter, request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Authentication should be null for API docs path");
    }

    @Test
    void testTokenFilter_NoAuthHeader() throws Exception {
        // Use a fresh instance of SecurityConfig to avoid interference
        SecurityConfig configForTest = new SecurityConfig();
        ReflectionTestUtils.setField(configForTest, "securityToken", TEST_TOKEN);

        // Create a fresh request and response
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/translations");
        // No Authorization header

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Get a filter from our fresh config instance
        OncePerRequestFilter filter = configForTest.tokenAuthenticationFilter();

        // Clear security context to ensure test isolation
        SecurityContextHolder.clearContext();

        // Act
        invokeDoFilterInternal(filter, request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Authentication should be null when no auth header is present");
    }

    @Test
    void testTokenFilter_NonBearerToken() throws Exception {
        // Use a fresh instance of SecurityConfig to avoid interference
        SecurityConfig configForTest = new SecurityConfig();
        ReflectionTestUtils.setField(configForTest, "securityToken", TEST_TOKEN);

        // Create a fresh request and response
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/translations");
        request.addHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Get a filter from our fresh config instance
        OncePerRequestFilter filter = configForTest.tokenAuthenticationFilter();

        // Clear security context to ensure test isolation
        SecurityContextHolder.clearContext();

        // Act
        invokeDoFilterInternal(filter, request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Authentication should be null for non-Bearer token");
    }

    @Test
    void testTokenFilter_ValidToken() throws Exception {
        // Use a fresh instance of SecurityConfig to avoid interference
        SecurityConfig configForTest = new SecurityConfig();
        ReflectionTestUtils.setField(configForTest, "securityToken", TEST_TOKEN);

        // Create a fresh request and response
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/translations");
        request.addHeader("Authorization", "Bearer " + TEST_TOKEN);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Get a filter from our fresh config instance
        OncePerRequestFilter filter = configForTest.tokenAuthenticationFilter();

        // Clear security context to ensure test isolation
        SecurityContextHolder.clearContext();

        // Act
        invokeDoFilterInternal(filter, request, response, filterChain);

        // Assert
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "Authentication should be set for valid token");
        assertEquals("translation-api-user", auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_API_USER")));
    }

    @Test
    void testTokenFilter_InvalidToken() throws Exception {
        // Use a fresh instance of SecurityConfig to avoid interference
        SecurityConfig configForTest = new SecurityConfig();
        ReflectionTestUtils.setField(configForTest, "securityToken", TEST_TOKEN);

        // Create a fresh request and response
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/translations");
        request.addHeader("Authorization", "Bearer invalid-token");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        // Get a filter from our fresh config instance
        OncePerRequestFilter filter = configForTest.tokenAuthenticationFilter();

        // Clear security context to ensure test isolation
        SecurityContextHolder.clearContext();

        // Act
        invokeDoFilterInternal(filter, request, response, filterChain);

        // Assert
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus(),
                "Response status should be 401 Unauthorized for invalid token");
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "Authentication should be null for invalid token");
    }


    // Helper method to invoke protected doFilterInternal method via reflection
    private void invokeDoFilterInternal(OncePerRequestFilter filter, HttpServletRequest request,
                                        HttpServletResponse response, FilterChain filterChain)
            throws Exception {

        Method method = OncePerRequestFilter.class.getDeclaredMethod(
                "doFilterInternal",
                HttpServletRequest.class,
                HttpServletResponse.class,
                FilterChain.class);

        method.setAccessible(true);
        method.invoke(filter, request, response, filterChain);
    }
}