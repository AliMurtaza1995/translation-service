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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TranslationServiceTest {

    @Mock
    private TranslationRepository translationRepository;

    @Mock
    private LocaleRepository localeRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TranslationService translationService;

    private Locale testLocale;
    private Translation testTranslation;
    private Tag testTag;

    @BeforeEach
    public void setUp() {
        // Setup test data
        testLocale = new Locale();
        testLocale.setId(1L);
        testLocale.setCode("en");
        testLocale.setName("English");

        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("mobile");

        testTranslation = Translation.builder()
                .id(1L)
                .key("test.key")
                .content("Test Translation")
                .locale(testLocale)
                .build();
        testTranslation.addTag(testTag);
    }

    @Test
    public void testCreateTranslation() {
        // Prepare
        TranslationRequestDTO requestDTO = createTestTranslationRequestDTO();

        when(localeRepository.findByCode(anyString()))
                .thenReturn(Optional.of(testLocale));

        when(translationRepository.findByKeyAndLocale(anyString(), any(Locale.class)))
                .thenReturn(Optional.empty());

        when(tagRepository.findByName(anyString()))
                .thenReturn(Optional.of(testTag));

        when(translationRepository.save(any(Translation.class)))
                .thenReturn(testTranslation);

        // Execute
        TranslationResponseDTO responseDTO = translationService.createTranslation(requestDTO);

        // Verify
        assertNotNull(responseDTO);
        assertEquals("test.key", responseDTO.getKey());
        assertEquals("en", responseDTO.getLocaleCode());
        verify(translationRepository).save(any(Translation.class));
    }

    @Test
    public void testCreateTranslation_DuplicateTranslation() {
        // Prepare
        TranslationRequestDTO requestDTO = createTestTranslationRequestDTO();

        when(localeRepository.findByCode(anyString()))
                .thenReturn(Optional.of(testLocale));

        when(translationRepository.findByKeyAndLocale(anyString(), any(Locale.class)))
                .thenReturn(Optional.of(testTranslation));

        // Execute & Verify
        assertThrows(IllegalArgumentException.class, () -> {
            translationService.createTranslation(requestDTO);
        });
    }

    @Test
    public void testUpdateTranslation() {
        // Prepare
        TranslationRequestDTO requestDTO = createTestTranslationRequestDTO();
        requestDTO.setContent("Updated Translation");

        when(translationRepository.findById(anyLong()))
                .thenReturn(Optional.of(testTranslation));

        when(localeRepository.findByCode(anyString()))
                .thenReturn(Optional.of(testLocale));

        when(tagRepository.findByName(anyString()))
                .thenReturn(Optional.of(testTag));

        when(translationRepository.save(any(Translation.class)))
                .thenReturn(testTranslation);

        // Execute
        TranslationResponseDTO responseDTO = translationService.updateTranslation(1L, requestDTO);

        // Verify
        assertNotNull(responseDTO);
        assertEquals("Updated Translation", responseDTO.getContent());
        verify(translationRepository).save(any(Translation.class));
    }

    @Test
    public void testGetTranslation() {
        // Prepare
        when(translationRepository.findById(anyLong()))
                .thenReturn(Optional.of(testTranslation));

        // Execute
        TranslationResponseDTO responseDTO = translationService.getTranslation(1L);

        // Verify
        assertNotNull(responseDTO);
        assertEquals("test.key", responseDTO.getKey());
    }

    @Test
    public void testGetTranslation_NotFound() {
        // Prepare
        when(translationRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows(ResourceNotFoundException.class, () -> {
            translationService.getTranslation(1L);
        });
    }

    @Test
    public void testGetAllTranslations() {
        // Prepare
        List<Translation> translations = Arrays.asList(testTranslation);
        Page<Translation> translationPage = new PageImpl<>(translations);

        when(translationRepository.findAll(any(Pageable.class)))
                .thenReturn(translationPage);

        // Execute
        List<TranslationResponseDTO> responseDTOs = translationService.getAllTranslations(PageRequest.of(0, 20));

        // Verify
        assertNotNull(responseDTOs);
        assertEquals(1, responseDTOs.size());
    }

    @Test
    public void testGetTranslationsByLocale() {
        // Prepare
        when(localeRepository.findByCode(anyString()))
                .thenReturn(Optional.of(testLocale));

        when(translationRepository.findByLocale(any(Locale.class)))
                .thenReturn(Arrays.asList(testTranslation));

        // Execute
        Map<String, String> translations = translationService.getTranslationsByLocale("en");

        // Verify
        assertNotNull(translations);
        assertTrue(translations.containsKey("test.key"));
        assertEquals("Test Translation", translations.get("test.key"));
    }

    @Test
    public void testSearchTranslations() {
        // Prepare
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setKey("test");
        searchRequest.setTags(Arrays.asList("mobile"));

        List<Translation> translations = Arrays.asList(testTranslation);
        Page<Translation> translationPage = new PageImpl<>(translations);

        // Use lenient() to allow flexible matching or use exact matching
        lenient().when(translationRepository.searchTranslations(
                eq("test"),  // Exact match for key
                isNull(),    // null for content
                isNull(),    // null for localeCode
                eq(Arrays.asList("mobile")),  // Exact match for tags
                eq(1),       // Tag count
                any(Pageable.class)
        )).thenReturn(translationPage);

        // Execute
        Page<TranslationResponseDTO> responsePage = translationService.searchTranslations(searchRequest);

        // Verify
        assertNotNull(responsePage);
        assertEquals(1, responsePage.getContent().size());
        verify(translationRepository).searchTranslations(
                eq("test"),
                isNull(),
                isNull(),
                eq(Arrays.asList("mobile")),
                eq(1),
                any(Pageable.class)
        );
    }

    @Test
    public void testDeleteTranslation() {
        // Prepare
        when(translationRepository.existsById(anyLong()))
                .thenReturn(true);

        // Execute
        translationService.deleteTranslation(1L);

        // Verify
        verify(translationRepository).deleteById(1L);
    }

    @Test
    public void testDeleteTranslation_NotFound() {
        // Prepare
        when(translationRepository.existsById(anyLong()))
                .thenReturn(false);

        // Execute & Verify
        assertThrows(ResourceNotFoundException.class, () -> {
            translationService.deleteTranslation(1L);
        });
    }

    // Helper method to create a test TranslationRequestDTO
    private TranslationRequestDTO createTestTranslationRequestDTO() {
        TranslationRequestDTO dto = new TranslationRequestDTO();
        dto.setKey("test.key");
        dto.setContent("Test Translation");
        dto.setLocaleCode("en");
        dto.setTags(Arrays.asList("mobile"));
        return dto;
    }

    @Test
    public void testGetTranslationsByTagName() {
        // Prepare
        when(translationRepository.findByTagName(anyString()))
                .thenReturn(Arrays.asList(testTranslation));

        // Execute
        List<TranslationResponseDTO> responseDTOs = translationService.getTranslationsByTagName("mobile");

        // Verify
        assertNotNull(responseDTOs);
        assertEquals(1, responseDTOs.size());
        assertEquals("test.key", responseDTOs.get(0).getKey());
    }

    @Test
    public void testGetTranslationsByKeyPattern() {
        // Prepare
        when(translationRepository.findByKeyContaining(anyString()))
                .thenReturn(Arrays.asList(testTranslation));

        // Execute
        List<TranslationResponseDTO> responseDTOs = translationService.getTranslationsByKeyPattern("test");

        // Verify
        assertNotNull(responseDTOs);
        assertEquals(1, responseDTOs.size());
        assertEquals("test.key", responseDTOs.get(0).getKey());
    }

    @Test
    public void testGetTranslationsByContentPattern() {
        // Prepare
        when(translationRepository.findByContentContaining(anyString()))
                .thenReturn(Arrays.asList(testTranslation));

        // Execute
        List<TranslationResponseDTO> responseDTOs = translationService.getTranslationsByContentPattern("Translation");

        // Verify
        assertNotNull(responseDTOs);
        assertEquals(1, responseDTOs.size());
        assertEquals("Test Translation", responseDTOs.get(0).getContent());
    }
}