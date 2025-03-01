package com.translationservice.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LocaleTest {
    @Test
    public void testLocaleConstructors() {
        // Default constructor
        Locale defaultLocale = new Locale();
        assertNotNull(defaultLocale);

        // Parameterized constructor
        LocalDateTime now = LocalDateTime.now();
        Locale locale = new Locale(
                1L,
                "en",
                "English",
                now,
                now
        );

        assertEquals(1L, locale.getId());
        assertEquals("en", locale.getCode());
        assertEquals("English", locale.getName());
        assertEquals(now, locale.getCreatedAt());
        assertEquals(now, locale.getUpdatedAt());
    }

    @Test
    public void testLocaleBuilder() {
        LocalDateTime now = LocalDateTime.now();

        Locale locale = Locale.builder()
                .id(1L)
                .code("fr")
                .name("French")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, locale.getId());
        assertEquals("fr", locale.getCode());
        assertEquals("French", locale.getName());
        assertEquals(now, locale.getCreatedAt());
        assertEquals(now, locale.getUpdatedAt());
    }

    @Test
    public void testPrePersistAndPreUpdate() {
        Locale locale = new Locale();

        // Simulate pre-persist
        locale.onCreate();
        assertNotNull(locale.getCreatedAt());
        assertNotNull(locale.getUpdatedAt());

        // Simulate pre-update
        LocalDateTime initialCreatedAt = locale.getCreatedAt();
        locale.onUpdate();
        assertEquals(initialCreatedAt, locale.getCreatedAt());
        assertNotNull(locale.getUpdatedAt());
    }
}