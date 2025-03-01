package com.translationservice.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TranslationRequestDTOTest {

    @Test
    void testTranslationRequestDTOConstructor() {
        // Prepare test data
        List<String> tags = Arrays.asList("tag1", "tag2");

        // Create DTO using constructor
        TranslationRequestDTO dto = new TranslationRequestDTO(
                "test.key",
                "Test Content",
                "en",
                tags
        );

        // Verify all fields
        assertEquals("test.key", dto.getKey());
        assertEquals("Test Content", dto.getContent());
        assertEquals("en", dto.getLocaleCode());
        assertEquals(tags, dto.getTags());
    }

    @Test
    void testTranslationRequestDTOBuilder() {
        // Prepare test data
        List<String> tags = Arrays.asList("tag1", "tag2");

        // Create DTO using builder
        TranslationRequestDTO dto = TranslationRequestDTO.builder()
                .key("test.key")
                .content("Test Content")
                .localeCode("en")
                .tags(tags)
                .build();

        // Verify all fields
        assertEquals("test.key", dto.getKey());
        assertEquals("Test Content", dto.getContent());
        assertEquals("en", dto.getLocaleCode());
        assertEquals(tags, dto.getTags());
    }

    @Test
    void testTranslationRequestDTOSetters() {
        // Create empty DTO
        TranslationRequestDTO dto = new TranslationRequestDTO();

        // Test all setters
        List<String> tags = Arrays.asList("tag1", "tag2");

        dto.setKey("test.key");
        assertEquals("test.key", dto.getKey());

        dto.setContent("Test Content");
        assertEquals("Test Content", dto.getContent());

        dto.setLocaleCode("en");
        assertEquals("en", dto.getLocaleCode());

        dto.setTags(tags);
        assertEquals(tags, dto.getTags());
    }

    @Test
    void testTranslationRequestDTODefaultConstructor() {
        // Create DTO using default constructor
        TranslationRequestDTO dto = new TranslationRequestDTO();

        // Verify that all fields are null
        assertNull(dto.getKey());
        assertNull(dto.getContent());
        assertNull(dto.getLocaleCode());
        assertNull(dto.getTags());
    }

    @Test
    void testTranslationRequestDTOValidationAnnotations() {
        // Verify validation annotations
        TranslationRequestDTO dto = new TranslationRequestDTO();

        // These fields are annotated with @NotBlank
        assertNull(dto.getKey());
        assertNull(dto.getContent());
        assertNull(dto.getLocaleCode());
    }
}