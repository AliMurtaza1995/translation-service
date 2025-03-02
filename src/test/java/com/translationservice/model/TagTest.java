package com.translationservice.model;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    private static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

    @Test
    void testEmptyConstructor() {
        // Act
        Tag tag = new Tag();

        // Assert
        assertNull(tag.getId());
        assertNull(tag.getName());
        assertNull(tag.getCreatedAt());
        assertNull(tag.getUpdatedAt());
        assertNotNull(tag.getTranslations());
        assertTrue(tag.getTranslations().isEmpty());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        Long id = 1L;
        String name = "test-tag";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        Set<Translation> translations = new HashSet<>();
        Translation translation = new Translation();
        translation.setId(1L);
        translations.add(translation);

        // Act
        Tag tag = new Tag(id, name, createdAt, updatedAt, translations);

        // Assert
        assertEquals(id, tag.getId());
        assertEquals(name, tag.getName());
        assertEquals(createdAt, tag.getCreatedAt());
        assertEquals(updatedAt, tag.getUpdatedAt());
        assertEquals(translations, tag.getTranslations());
        assertEquals(1, tag.getTranslations().size());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        Tag tag = new Tag();
        Long id = 1L;
        String name = "test-tag";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        Set<Translation> translations = new HashSet<>();
        Translation translation = new Translation();
        translation.setId(1L);
        translations.add(translation);

        // Act
        tag.setId(id);
        tag.setName(name);
        tag.setCreatedAt(createdAt);
        tag.setUpdatedAt(updatedAt);
        tag.setTranslations(translations);

        // Assert
        assertEquals(id, tag.getId());
        assertEquals(name, tag.getName());
        assertEquals(createdAt, tag.getCreatedAt());
        assertEquals(updatedAt, tag.getUpdatedAt());
        assertEquals(translations, tag.getTranslations());
        assertEquals(1, tag.getTranslations().size());
    }

    @Test
    void testBuilder() {
        // Arrange
        Long id = 1L;
        String name = "test-tag";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        Set<Translation> translations = new HashSet<>();
        Translation translation = new Translation();
        translation.setId(1L);
        translations.add(translation);

        // Act
        Tag tag = Tag.builder()
                .id(id)
                .name(name)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .translations(translations)
                .build();

        // Assert
        assertEquals(id, tag.getId());
        assertEquals(name, tag.getName());
        assertEquals(createdAt, tag.getCreatedAt());
        assertEquals(updatedAt, tag.getUpdatedAt());
        assertEquals(translations, tag.getTranslations());
        assertEquals(1, tag.getTranslations().size());
    }

    @Test
    void testBuilderNoValues() {
        // Act
        Tag tag = Tag.builder().build();

        // Assert
        assertNull(tag.getId());
        assertNull(tag.getName());
        assertNull(tag.getCreatedAt());
        assertNull(tag.getUpdatedAt());
        assertNotNull(tag.getTranslations());
        assertTrue(tag.getTranslations().isEmpty());
    }

    @Test
    void testPrePersist() {
        // Arrange
        Tag tag = new Tag();

        // Act - using try-with-resources to mock the static LocalDateTime.now()
        try (MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(FIXED_DATE_TIME);

            tag.onCreate();

            // Assert
            assertEquals(FIXED_DATE_TIME, tag.getCreatedAt());
            assertEquals(FIXED_DATE_TIME, tag.getUpdatedAt());
        }
    }

    @Test
    void testPreUpdate() {
        // Arrange
        Tag tag = new Tag();
        LocalDateTime createdAt = LocalDateTime.of(2022, 1, 1, 12, 0, 0);
        tag.setCreatedAt(createdAt);

        // Act - using try-with-resources to mock the static LocalDateTime.now()
        try (MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(FIXED_DATE_TIME);

            tag.onUpdate();

            // Assert
            assertEquals(createdAt, tag.getCreatedAt(), "Created date should not change on update");
            assertEquals(FIXED_DATE_TIME, tag.getUpdatedAt(), "Updated date should change to current time");
        }
    }
}