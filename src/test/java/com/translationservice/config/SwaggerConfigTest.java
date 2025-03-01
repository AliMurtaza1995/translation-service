package com.translationservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.junit.jupiter.api.Test;
import org.springdoc.core.models.GroupedOpenApi;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    @Test
    void testSwaggerConfigAnnotations() {
        // Verify @Configuration annotation
        assertNotNull(SwaggerConfig.class.getAnnotation(org.springframework.context.annotation.Configuration.class));

        // Verify @OpenAPIDefinition annotation
        OpenAPIDefinition openApiDefinition = SwaggerConfig.class.getAnnotation(OpenAPIDefinition.class);
        assertNotNull(openApiDefinition);

        // Verify OpenAPI Info
        Info info = openApiDefinition.info();
        assertEquals("Translation Management Service API", info.title());
        assertEquals("1.0.0", info.version());
        assertEquals("API for managing translations across multiple locales", info.description());

        // Verify Contact
        Contact contact = info.contact();
        assertEquals("Translation Service Support", contact.name());
        assertEquals("support@translationservice.com", contact.email());

        // Verify Servers
        Server[] servers = openApiDefinition.servers();
        assertEquals(1, servers.length);
        assertEquals("http://localhost:${server.port}", servers[0].url());
        assertEquals("Local development server", servers[0].description());

        // Verify SecurityScheme
        SecurityScheme securityScheme = SwaggerConfig.class.getAnnotation(SecurityScheme.class);
        assertNotNull(securityScheme);
        assertEquals("bearerToken", securityScheme.name());
    }

    @Test
    void testPublicApiConfiguration() {
        // Create Swagger config
        SwaggerConfig swaggerConfig = new SwaggerConfig();

        // Get grouped open API
        GroupedOpenApi publicApi = swaggerConfig.publicApi();

        // Verify public API configuration
        assertNotNull(publicApi);
        assertEquals("public", publicApi.getGroup());
    }
}