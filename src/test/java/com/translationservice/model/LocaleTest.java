package com.translationservice.model;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LocaleTest {

    private static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

    @Test
    void testEmptyConstructor() {
        Locale locale = new Locale();

        assertNull(locale.getId());
        assertNull(locale.getCode());
        assertNull(locale.getName());
        assertNull(locale.getCreatedAt());
        assertNull(locale.getUpdatedAt());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        Long id = 1L;
        String code = "en-US";
        String name = "English (US)";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        Locale locale = new Locale(id, code, name, createdAt, updatedAt);

        // Assert
        assertEquals(id, locale.getId());
        assertEquals(code, locale.getCode());
        assertEquals(name, locale.getName());
        assertEquals(createdAt, locale.getCreatedAt());
        assertEquals(updatedAt, locale.getUpdatedAt());
    }

    @Test
    void testGettersAndSetters() {
        // Arrange
        Locale locale = new Locale();
        Long id = 1L;
        String code = "en-US";
        String name = "English (US)";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        locale.setId(id);
        locale.setCode(code);
        locale.setName(name);
        locale.setCreatedAt(createdAt);
        locale.setUpdatedAt(updatedAt);

        // Assert
        assertEquals(id, locale.getId());
        assertEquals(code, locale.getCode());
        assertEquals(name, locale.getName());
        assertEquals(createdAt, locale.getCreatedAt());
        assertEquals(updatedAt, locale.getUpdatedAt());
    }

    @Test
    void testBuilder() {
        // Arrange
        Long id = 1L;
        String code = "en-US";
        String name = "English (US)";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        Locale locale = Locale.builder()
                .id(id)
                .code(code)
                .name(name)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // Assert
        assertEquals(id, locale.getId());
        assertEquals(code, locale.getCode());
        assertEquals(name, locale.getName());
        assertEquals(createdAt, locale.getCreatedAt());
        assertEquals(updatedAt, locale.getUpdatedAt());
    }

    @Test
    void testBuilderNoValues() {
        // Act
        Locale locale = Locale.builder().build();

        // Assert
        assertNull(locale.getId());
        assertNull(locale.getCode());
        assertNull(locale.getName());
        assertNull(locale.getCreatedAt());
        assertNull(locale.getUpdatedAt());
    }

    @Test
    void testPrePersist() {
        // Arrange
        Locale locale = new Locale();

        // Act - using try-with-resources to mock the static LocalDateTime.now()
        try (MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(FIXED_DATE_TIME);

            locale.onCreate();

            // Assert
            assertEquals(FIXED_DATE_TIME, locale.getCreatedAt());
            assertEquals(FIXED_DATE_TIME, locale.getUpdatedAt());
        }
    }

    @Test
    void testPreUpdate() {
        // Arrange
        Locale locale = new Locale();
        LocalDateTime createdAt = LocalDateTime.of(2022, 1, 1, 12, 0, 0);
        locale.setCreatedAt(createdAt);

        // Act - using try-with-resources to mock the static LocalDateTime.now()
        try (MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(FIXED_DATE_TIME);

            locale.onUpdate();

            // Assert
            assertEquals(createdAt, locale.getCreatedAt(), "Created date should not change on update");
            assertEquals(FIXED_DATE_TIME, locale.getUpdatedAt(), "Updated date should change to current time");
        }
    }
}