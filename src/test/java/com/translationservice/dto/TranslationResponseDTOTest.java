package com.translationservice.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TranslationResponseDTOTest {

    @Test
    void testTranslationResponseDTOConstructor() {
        // Prepare test data
        LocalDateTime now = LocalDateTime.now();
        List<String> tags = Arrays.asList("tag1", "tag2");

        // Create DTO using constructor
        TranslationResponseDTO dto = new TranslationResponseDTO(
                1L,
                "test.key",
                "Test Content",
                "en",
                "English",
                tags,
                now,
                now
        );

        // Verify all fields
        assertEquals(1L, dto.getId());
        assertEquals("test.key", dto.getKey());
        assertEquals("Test Content", dto.getContent());
        assertEquals("en", dto.getLocaleCode());
        assertEquals("English", dto.getLocaleName());
        assertEquals(tags, dto.getTags());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testTranslationResponseDTOBuilder() {
        // Prepare test data
        LocalDateTime now = LocalDateTime.now();
        List<String> tags = Arrays.asList("tag1", "tag2");

        // Create DTO using builder
        TranslationResponseDTO dto = TranslationResponseDTO.builder()
                .id(1L)
                .key("test.key")
                .content("Test Content")
                .localeCode("en")
                .localeName("English")
                .tags(tags)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Verify all fields
        assertEquals(1L, dto.getId());
        assertEquals("test.key", dto.getKey());
        assertEquals("Test Content", dto.getContent());
        assertEquals("en", dto.getLocaleCode());
        assertEquals("English", dto.getLocaleName());
        assertEquals(tags, dto.getTags());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testTranslationResponseDTOSetters() {
        // Create empty DTO
        TranslationResponseDTO dto = new TranslationResponseDTO();

        // Test all setters
        LocalDateTime now = LocalDateTime.now();
        List<String> tags = Arrays.asList("tag1", "tag2");

        dto.setId(1L);
        assertEquals(1L, dto.getId());

        dto.setKey("test.key");
        assertEquals("test.key", dto.getKey());

        dto.setContent("Test Content");
        assertEquals("Test Content", dto.getContent());

        dto.setLocaleCode("en");
        assertEquals("en", dto.getLocaleCode());

        dto.setLocaleName("English");
        assertEquals("English", dto.getLocaleName());

        dto.setTags(tags);
        assertEquals(tags, dto.getTags());

        dto.setCreatedAt(now);
        assertEquals(now, dto.getCreatedAt());

        dto.setUpdatedAt(now);
        assertEquals(now, dto.getUpdatedAt());
    }

    @Test
    void testTranslationResponseDTODefaultConstructor() {
        // Create DTO using default constructor
        TranslationResponseDTO dto = new TranslationResponseDTO();

        // Verify that all fields are null/empty
        assertNull(dto.getId());
        assertNull(dto.getKey());
        assertNull(dto.getContent());
        assertNull(dto.getLocaleCode());
        assertNull(dto.getLocaleName());
        assertNull(dto.getTags());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }
}