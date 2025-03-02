package com.translationservice.model;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TranslationTest {

    private static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.of(2023, 1, 1, 12, 0, 0);

    @Test
    void testEmptyConstructor() {

        Translation translation = new Translation();


        assertNull(translation.getId());
        assertNull(translation.getKey());
        assertNull(translation.getContent());
        assertNull(translation.getLocale());
        assertNull(translation.getCreatedAt());
        assertNull(translation.getUpdatedAt());
        assertNotNull(translation.getTags());
        assertTrue(translation.getTags().isEmpty());
    }

    @Test
    void testParameterizedConstructor() {

        Long id = 1L;
        String key = "test.key";
        String content = "Test content";
        Locale locale = new Locale();
        locale.setId(1L);
        locale.setCode("en-US");
        Set<Tag> tags = new HashSet<>();
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test-tag");
        tags.add(tag);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();


        Translation translation = new Translation(id, key, content, locale, tags, createdAt, updatedAt);


        assertEquals(id, translation.getId());
        assertEquals(key, translation.getKey());
        assertEquals(content, translation.getContent());
        assertEquals(locale, translation.getLocale());
        assertEquals(tags, translation.getTags());
        assertEquals(createdAt, translation.getCreatedAt());
        assertEquals(updatedAt, translation.getUpdatedAt());
        assertEquals(1, translation.getTags().size());
    }

    @Test
    void testGettersAndSetters() {

        Translation translation = new Translation();
        Long id = 1L;
        String key = "test.key";
        String content = "Test content";
        Locale locale = new Locale();
        locale.setId(1L);
        locale.setCode("en-US");
        Set<Tag> tags = new HashSet<>();
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test-tag");
        tags.add(tag);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();


        translation.setId(id);
        translation.setKey(key);
        translation.setContent(content);
        translation.setLocale(locale);
        translation.setTags(tags);
        translation.setCreatedAt(createdAt);
        translation.setUpdatedAt(updatedAt);


        assertEquals(id, translation.getId());
        assertEquals(key, translation.getKey());
        assertEquals(content, translation.getContent());
        assertEquals(locale, translation.getLocale());
        assertEquals(tags, translation.getTags());
        assertEquals(createdAt, translation.getCreatedAt());
        assertEquals(updatedAt, translation.getUpdatedAt());
    }

    @Test
    void testBuilder() {

        Long id = 1L;
        String key = "test.key";
        String content = "Test content";
        Locale locale = new Locale();
        locale.setId(1L);
        locale.setCode("en-US");
        Set<Tag> tags = new HashSet<>();
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test-tag");
        tags.add(tag);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();


        Translation translation = Translation.builder()
                .id(id)
                .key(key)
                .content(content)
                .locale(locale)
                .tags(tags)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();


        assertEquals(id, translation.getId());
        assertEquals(key, translation.getKey());
        assertEquals(content, translation.getContent());
        assertEquals(locale, translation.getLocale());
        assertEquals(tags, translation.getTags());
        assertEquals(createdAt, translation.getCreatedAt());
        assertEquals(updatedAt, translation.getUpdatedAt());
        assertEquals(1, translation.getTags().size());
    }

    @Test
    void testBuilderNoValues() {

        Translation translation = Translation.builder().build();


        assertNull(translation.getId());
        assertNull(translation.getKey());
        assertNull(translation.getContent());
        assertNull(translation.getLocale());
        assertNull(translation.getCreatedAt());
        assertNull(translation.getUpdatedAt());
        assertNotNull(translation.getTags());
        assertTrue(translation.getTags().isEmpty());
    }

    @Test
    void testAddTag() {

        Translation translation = new Translation();
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test-tag");


        translation.addTag(tag);


        assertEquals(1, translation.getTags().size());
        assertTrue(translation.getTags().contains(tag));
    }

    @Test
    void testRemoveTag() {

        Translation translation = new Translation();
        Tag tag = new Tag();
        tag.setId(1L);
        tag.setName("test-tag");


        Set<Translation> translations = new HashSet<>();
        translations.add(translation);
        tag.setTranslations(translations);

        translation.addTag(tag);
        assertEquals(1, translation.getTags().size());


        translation.removeTag(tag);


        assertEquals(0, translation.getTags().size());
        assertFalse(translation.getTags().contains(tag));
        assertFalse(tag.getTranslations().contains(translation));
    }

    @Test
    void testPrePersist() {

        Translation translation = new Translation();

        try (MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(FIXED_DATE_TIME);

            translation.onCreate();

            assertEquals(FIXED_DATE_TIME, translation.getCreatedAt());
            assertEquals(FIXED_DATE_TIME, translation.getUpdatedAt());
        }
    }

    @Test
    void testPreUpdate() {
        Translation translation = new Translation();
        LocalDateTime createdAt = LocalDateTime.of(2022, 1, 1, 12, 0, 0);
        translation.setCreatedAt(createdAt);

        try (MockedStatic<LocalDateTime> mockedStatic = Mockito.mockStatic(LocalDateTime.class)) {
            mockedStatic.when(LocalDateTime::now).thenReturn(FIXED_DATE_TIME);

            translation.onUpdate();

            assertEquals(createdAt, translation.getCreatedAt(), "Created date should not change on update");
            assertEquals(FIXED_DATE_TIME, translation.getUpdatedAt(), "Updated date should change to current time");
        }
    }
}