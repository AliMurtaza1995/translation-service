package com.translationservice.service;


import com.translationservice.dto.SearchRequestDTO;
import com.translationservice.dto.TranslationRequestDTO;
import com.translationservice.dto.TranslationResponseDTO;
import com.translationservice.exception.ResourceNotFoundException;
import com.translationservice.model.Locale;
import com.translationservice.model.Tag;
import com.translationservice.model.Translation;
import com.translationservice.repository.LocaleRepository;
import com.translationservice.repository.TagRepository;
import com.translationservice.repository.TranslationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

    @Mock
    private TranslationRepository translationRepository;

    @Mock
    private LocaleRepository localeRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TranslationService translationService;

    private Locale locale;
    private Translation translation;
    private TranslationRequestDTO requestDTO;
    private Tag tag;

    @BeforeEach
    void setUp() {
        locale = new Locale();
        locale.setId(1L);
        locale.setCode("en");
        locale.setName("English");

        tag = new Tag();
        tag.setId(1L);
        tag.setName("testTag");

        translation = Translation.builder()
                .id(1L)
                .key("test.key")
                .content("Test Content")
                .locale(locale)
                .tags(new HashSet<>(Collections.singletonList(tag)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestDTO = TranslationRequestDTO.builder()
                .key("test.key")
                .content("Test Content")
                .localeCode("en")
                .tags(Collections.singletonList("testTag"))
                .build();
    }

    @Test
    void testCreateTranslation() {
        // Prepare mocks
        when(localeRepository.findByCode("en")).thenReturn(Optional.of(locale));
        when(translationRepository.findByKeyAndLocale(anyString(), any(Locale.class))).thenReturn(Optional.empty());
        when(tagRepository.findByName("testTag")).thenReturn(Optional.of(tag));
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);

        // Execute
        TranslationResponseDTO responseDTO = translationService.createTranslation(requestDTO);

        // Verify
        assertNotNull(responseDTO);
        assertEquals("test.key", responseDTO.getKey());
        verify(translationRepository).save(any(Translation.class));
    }

    @Test
    void testCreateTranslationWithNewTag() {
        // Prepare mocks
        when(localeRepository.findByCode("en")).thenReturn(Optional.of(locale));
        when(translationRepository.findByKeyAndLocale(anyString(), any(Locale.class))).thenReturn(Optional.empty());
        when(tagRepository.findByName("testTag")).thenReturn(Optional.empty());

        Tag newTag = new Tag();
        newTag.setName("testTag");
        when(tagRepository.save(any(Tag.class))).thenReturn(newTag);
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);

        // Execute
        TranslationResponseDTO responseDTO = translationService.createTranslation(requestDTO);

        // Verify
        assertNotNull(responseDTO);
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void testCreateTranslationWithDuplicateKey() {
        // Prepare mocks
        when(localeRepository.findByCode("en")).thenReturn(Optional.of(locale));
        when(translationRepository.findByKeyAndLocale(anyString(), any(Locale.class)))
                .thenReturn(Optional.of(translation));

        // Execute and verify
        assertThrows(IllegalArgumentException.class,
                () -> translationService.createTranslation(requestDTO));
    }

    @Test
    void testCreateTranslationWithNonExistentLocale() {
        // Prepare mocks
        when(localeRepository.findByCode("en")).thenReturn(Optional.empty());

        // Execute and verify
        assertThrows(ResourceNotFoundException.class,
                () -> translationService.createTranslation(requestDTO));
    }

    @Test
    void testUpdateTranslation() {
        // Prepare mocks
        when(translationRepository.findById(1L)).thenReturn(Optional.of(translation));
        when(localeRepository.findByCode("en")).thenReturn(Optional.of(locale));
        when(tagRepository.findByName("testTag")).thenReturn(Optional.of(tag));
        when(translationRepository.save(any(Translation.class))).thenReturn(translation);

        // Execute
        TranslationResponseDTO responseDTO = translationService.updateTranslation(1L, requestDTO);

        // Verify
        assertNotNull(responseDTO);
        assertEquals("test.key", responseDTO.getKey());
        verify(translationRepository).save(any(Translation.class));
    }

    @Test
    void testUpdateTranslationWithNonExistentTranslation() {
        // Prepare mocks
        when(translationRepository.findById(1L)).thenReturn(Optional.empty());

        // Execute and verify
        assertThrows(ResourceNotFoundException.class,
                () -> translationService.updateTranslation(1L, requestDTO));
    }

    @Test
    void testGetTranslation() {
        // Prepare mocks
        when(translationRepository.findById(1L)).thenReturn(Optional.of(translation));

        // Execute
        TranslationResponseDTO responseDTO = translationService.getTranslation(1L);

        // Verify
        assertNotNull(responseDTO);
        assertEquals("test.key", responseDTO.getKey());
    }

    @Test
    void testGetTranslationNotFound() {
        // Prepare mocks
        when(translationRepository.findById(1L)).thenReturn(Optional.empty());

        // Execute and verify
        assertThrows(ResourceNotFoundException.class,
                () -> translationService.getTranslation(1L));
    }

    @Test
    void testGetAllTranslations() {
        // Prepare mocks
        List<Translation> translations = Collections.singletonList(translation);
        Page<Translation> translationPage = new PageImpl<>(translations);
        when(translationRepository.findAll(any(Pageable.class))).thenReturn(translationPage);

        // Execute
        List<TranslationResponseDTO> responseDTOs = translationService.getAllTranslations(PageRequest.of(0, 20));

        // Verify
        assertFalse(responseDTOs.isEmpty());
        assertEquals(1, responseDTOs.size());
    }

    @Test
    void testGetTranslationsByLocale() {
        // Prepare mocks
        when(localeRepository.findByCode("en")).thenReturn(Optional.of(locale));
        when(translationRepository.findByLocale(locale)).thenReturn(Collections.singletonList(translation));

        // Execute
        Map<String, String> translations = translationService.getTranslationsByLocale("en");

        // Verify
        assertFalse(translations.isEmpty());
        assertEquals("Test Content", translations.get("test.key"));
    }

    @Test
    void testGetTranslationsByLocaleNotFound() {
        // Prepare mocks
        when(localeRepository.findByCode("en")).thenReturn(Optional.empty());

        // Execute and verify
        assertThrows(ResourceNotFoundException.class,
                () -> translationService.getTranslationsByLocale("en"));
    }

    @Test
    void testGetAllTranslationsJson() {
        // Prepare mocks
        List<Locale> locales = Collections.singletonList(locale);
        when(localeRepository.findAll()).thenReturn(locales);
        when(localeRepository.findByCode("en")).thenReturn(Optional.of(locale));
        when(translationRepository.findByLocale(locale)).thenReturn(Collections.singletonList(translation));

        // Execute
        Map<String, Map<String, String>> result = translationService.getAllTranslationsJson();

        // Verify
        assertFalse(result.isEmpty());
        assertTrue(result.containsKey("en"));
    }

    @Test
    void testSearchTranslations() {
        // Prepare mocks
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setKey("test");
        searchRequest.setContent("Content");
        searchRequest.setLocaleCode("en");
        searchRequest.setTags(Collections.singletonList("testTag"));
        searchRequest.setPage(0);
        searchRequest.setSize(20);

        List<Translation> translations = Collections.singletonList(translation);
        Page<Translation> translationPage = new PageImpl<>(translations);

        when(translationRepository.searchTranslations(
                anyString(), anyString(), anyString(), anyList(), anyInt(), any(Pageable.class)
        )).thenReturn(translationPage);

        // Execute
        Page<TranslationResponseDTO> result = translationService.searchTranslations(searchRequest);

        // Verify
        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testGetTranslationsByTagName() {
        // Prepare mocks
        when(translationRepository.findByTagName("testTag")).thenReturn(Collections.singletonList(translation));

        // Execute
        List<TranslationResponseDTO> result = translationService.getTranslationsByTagName("testTag");

        // Verify
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetTranslationsByKeyPattern() {
        // Prepare mocks
        when(translationRepository.findByKeyContaining("test")).thenReturn(Collections.singletonList(translation));

        // Execute
        List<TranslationResponseDTO> result = translationService.getTranslationsByKeyPattern("test");

        // Verify
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetTranslationsByContentPattern() {
        // Prepare mocks
        when(translationRepository.findByContentContaining("Content")).thenReturn(Collections.singletonList(translation));

        // Execute
        List<TranslationResponseDTO> result = translationService.getTranslationsByContentPattern("Content");

        // Verify
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteTranslation() {
        // Prepare mocks
        when(translationRepository.existsById(1L)).thenReturn(true);
        doNothing().when(translationRepository).deleteById(1L);

        // Execute
        translationService.deleteTranslation(1L);

        // Verify
        verify(translationRepository).deleteById(1L);
    }

    @Test
    void testDeleteTranslationNotFound() {
        // Prepare mocks
        when(translationRepository.existsById(1L)).thenReturn(false);

        // Execute and verify
        assertThrows(ResourceNotFoundException.class,
                () -> translationService.deleteTranslation(1L));
    }

    @Test
    void testSearchTranslationsWithNullParameters() {
        // Prepare mocks
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setPage(0);
        searchRequest.setSize(20);
        searchRequest.setTags(new ArrayList<>());

        List<Translation> translations = Collections.singletonList(translation);
        Page<Translation> translationPage = new PageImpl<>(translations);

        when(translationRepository.searchTranslations(
                isNull(),
                isNull(),
                isNull(),
                eq(new ArrayList<>()),
                eq(0),
                any(Pageable.class)
        )).thenReturn(translationPage);

        // Execute
        Page<TranslationResponseDTO> result = translationService.searchTranslations(searchRequest);

        // Verify
        assertFalse(result.isEmpty());
        assertEquals(1, result.getContent().size());
    }
}