package com.translationservice.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TranslationTest {@Test
public void testTranslationConstructors() {
    // Default constructor
    Translation defaultTranslation = new Translation();
    assertNotNull(defaultTranslation);

    // Parameterized constructor
    Locale locale = new Locale();
    Set<Tag> tags = new HashSet<>();
    LocalDateTime now = LocalDateTime.now();

    Translation translation = new Translation(
            1L,
            "test.key",
            "Test Content",
            locale,
            tags,
            now,
            now
    );

    assertEquals(1L, translation.getId());
    assertEquals("test.key", translation.getKey());
    assertEquals("Test Content", translation.getContent());
    assertEquals(locale, translation.getLocale());
    assertEquals(tags, translation.getTags());
    assertEquals(now, translation.getCreatedAt());
    assertEquals(now, translation.getUpdatedAt());
}

    @Test
    public void testTranslationBuilder() {
        Locale locale = new Locale();
        Set<Tag> tags = new HashSet<>();
        LocalDateTime now = LocalDateTime.now();

        Translation translation = Translation.builder()
                .id(1L)
                .key("test.key")
                .content("Test Content")
                .locale(locale)
                .tags(tags)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, translation.getId());
        assertEquals("test.key", translation.getKey());
        assertEquals("Test Content", translation.getContent());
        assertEquals(locale, translation.getLocale());
        assertEquals(tags, translation.getTags());
        assertEquals(now, translation.getCreatedAt());
        assertEquals(now, translation.getUpdatedAt());
    }

    @Test
    public void testAddAndRemoveTags() {
        Translation translation = new Translation();
        Tag tag1 = new Tag();
        tag1.setName("mobile");
        Tag tag2 = new Tag();
        tag2.setName("web");

        // Test add tag
        translation.addTag(tag1);
        assertTrue(translation.getTags().contains(tag1));

        // Test remove tag
        translation.removeTag(tag1);
        assertFalse(translation.getTags().contains(tag1));
    }

    @Test
    public void testPrePersistAndPreUpdate() {
        Translation translation = new Translation();

        // Simulate pre-persist
        translation.onCreate();
        assertNotNull(translation.getCreatedAt());
        assertNotNull(translation.getUpdatedAt());

        // Simulate pre-update
        LocalDateTime initialCreatedAt = translation.getCreatedAt();
        translation.onUpdate();
        assertEquals(initialCreatedAt, translation.getCreatedAt());
        assertNotNull(translation.getUpdatedAt());
    }
}