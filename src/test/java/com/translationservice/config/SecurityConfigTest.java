package com.translationservice.config;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    private SecurityConfig securityConfig;

    @Mock
    private HttpSecurity httpSecurity;

    @BeforeEach
    void setUp() {
        securityConfig = new SecurityConfig();
        ReflectionTestUtils.setField(securityConfig, "securityToken", "translation-service-token");
    }

    /*@Test
    void testSecurityFilterChain() throws Exception {
        SecurityContextHolder.clearContext();
        SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity);

        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).httpBasic(any());
        verify(httpSecurity).addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter.class));
        verify(httpSecurity).sessionManagement(any());

        assertNotNull(filterChain);
    }*/

    @Test
    void testTokenAuthenticationFilter_ValidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer translation-service-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        var filter = securityConfig.tokenAuthenticationFilter();
        filter.doFilter(request, response, (req, res) -> {});

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testTokenAuthenticationFilter_InvalidToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        var filter = securityConfig.tokenAuthenticationFilter();
        filter.doFilter(request, response, (req, res) -> {});

        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    @Test
    void testTokenAuthenticationFilter_SwaggerPaths() throws Exception {
        String[] swaggerPaths = {"/swagger-ui/index.html", "/v3/api-docs/swagger-config"};

        for (String path : swaggerPaths) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI(path);
            MockHttpServletResponse response = new MockHttpServletResponse();

            var filter = securityConfig.tokenAuthenticationFilter();
            filter.doFilter(request, response, (req, res) -> {});
        }
    }

    @Test
    void testTokenAuthenticationFilter_NoAuthHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        var filter = securityConfig.tokenAuthenticationFilter();
        filter.doFilter(request, response, (req, res) -> {});
    }

    @Test
    void testUserDetailsService() {
        var userDetailsService = securityConfig.userDetailsService();
        var userDetails = userDetailsService.loadUserByUsername("user");

        assertNotNull(userDetails);
        assertEquals("user", userDetails.getUsername());
    }
}