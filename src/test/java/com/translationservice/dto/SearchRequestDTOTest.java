package com.translationservice.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;


class SearchRequestDTOTest {

    @Test
    void testSearchRequestDTOConstructor() {
        // Prepare test data
        List<String> tags = Arrays.asList("tag1", "tag2");

        // Create DTO using constructor
        SearchRequestDTO dto = new SearchRequestDTO(
                "test.key",
                "Test Content",
                "en",
                tags,
                0,
                20
        );

        // Verify all fields
        assertEquals("test.key", dto.getKey());
        assertEquals("Test Content", dto.getContent());
        assertEquals("en", dto.getLocaleCode());
        assertEquals(tags, dto.getTags());
        assertEquals(0, dto.getPage());
        assertEquals(20, dto.getSize());
    }

    @Test
    void testSearchRequestDTOBuilder() {
        // Prepare test data
        List<String> tags = Arrays.asList("tag1", "tag2");

        // Create DTO using builder
        SearchRequestDTO dto = SearchRequestDTO.builder()
                .key("test.key")
                .content("Test Content")
                .localeCode("en")
                .tags(tags)
                .page(0)
                .size(20)
                .build();

        // Verify all fields
        assertEquals("test.key", dto.getKey());
        assertEquals("Test Content", dto.getContent());
        assertEquals("en", dto.getLocaleCode());
        assertEquals(tags, dto.getTags());
        assertEquals(0, dto.getPage());
        assertEquals(20, dto.getSize());
    }

    @Test
    void testSearchRequestDTOSetters() {
        // Create empty DTO
        SearchRequestDTO dto = new SearchRequestDTO();

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

        dto.setPage(0);
        assertEquals(0, dto.getPage());

        dto.setSize(20);
        assertEquals(20, dto.getSize());
    }

    @Test
    void testSearchRequestDTODefaultConstructor() {
        // Create DTO using default constructor
        SearchRequestDTO dto = new SearchRequestDTO();

        // Verify that all fields are null
        assertNull(dto.getKey());
        assertNull(dto.getContent());
        assertNull(dto.getLocaleCode());
        assertNull(dto.getTags());
        assertNull(dto.getPage());
        assertNull(dto.getSize());
    }
}