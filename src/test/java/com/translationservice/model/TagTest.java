package com.translationservice.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    @Test
    public void testTagConstructors() {
        // Default constructor
        Tag defaultTag = new Tag();
        assertNotNull(defaultTag);

        // Parameterized constructor
        Set<Translation> translations = new HashSet<>();
        LocalDateTime now = LocalDateTime.now();
        Tag tag = new Tag(
                1L,
                "mobile",
                now,
                now,
                translations
        );

        assertEquals(1L, tag.getId());
        assertEquals("mobile", tag.getName());
        assertEquals(now, tag.getCreatedAt());
        assertEquals(now, tag.getUpdatedAt());
        assertEquals(translations, tag.getTranslations());
    }

    @Test
    public void testTagBuilder() {
        Set<Translation> translations = new HashSet<>();
        LocalDateTime now = LocalDateTime.now();

        Tag tag = Tag.builder()
                .id(1L)
                .name("web")
                .createdAt(now)
                .updatedAt(now)
                .translations(translations)
                .build();

        assertEquals(1L, tag.getId());
        assertEquals("web", tag.getName());
        assertEquals(now, tag.getCreatedAt());
        assertEquals(now, tag.getUpdatedAt());
        assertEquals(translations, tag.getTranslations());
    }

    @Test
    public void testPrePersistAndPreUpdate() {
        Tag tag = new Tag();

        // Simulate pre-persist
        tag.onCreate();
        assertNotNull(tag.getCreatedAt());
        assertNotNull(tag.getUpdatedAt());

        // Simulate pre-update
        LocalDateTime initialCreatedAt = tag.getCreatedAt();
        tag.onUpdate();
        assertEquals(initialCreatedAt, tag.getCreatedAt());
        assertNotNull(tag.getUpdatedAt());
    }

    @Test
    public void testTranslationManagement() {
        Tag tag = new Tag();
        Translation translation1 = new Translation();
        Translation translation2 = new Translation();

        // Add translations
        Set<Translation> translations = new HashSet<>();
        translations.add(translation1);
        translations.add(translation2);

        tag.setTranslations(translations);

        // Verify
        assertEquals(2, tag.getTranslations().size());
        assertTrue(tag.getTranslations().contains(translation1));
        assertTrue(tag.getTranslations().contains(translation2));
    }
}