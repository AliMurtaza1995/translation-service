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
    private TranslationRequestDTO testRequestDTO;
    private SearchRequestDTO testSearchRequestDTO;

    @BeforeEach
    void setUp() {
        // Setup test locale
        testLocale = new Locale();
        testLocale.setId(1L);
        testLocale.setCode("en-US");
        testLocale.setName("English (US)");

        // Setup test tag
        testTag = new Tag();
        testTag.setId(1L);
        testTag.setName("test-tag");

        // Setup test translation
        testTranslation = Translation.builder()
                .id(1L)
                .key("test.key")
                .content("Test content")
                .locale(testLocale)
                .tags(new HashSet<>(Collections.singletonList(testTag)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Setup test request DTO
        testRequestDTO = TranslationRequestDTO.builder()
                .key("test.key")
                .content("Test content")
                .localeCode("en-US")
                .tags(Collections.singletonList("test-tag"))
                .build();

        // Setup test search request DTO
        testSearchRequestDTO = SearchRequestDTO.builder()
                .key("test")
                .content("content")
                .localeCode("en-US")
                .tags(Collections.singletonList("test-tag"))
                .page(0)
                .size(10)
                .build();
    }

    @Test
    void createTranslation_WithValidData_ReturnsTranslationResponseDTO() {
        // Arrange
        when(localeRepository.findByCode(anyString())).thenReturn(Optional.of(testLocale));
        when(translationRepository.findByKeyAndLocale(anyString(), any(Locale.class))).thenReturn(Optional.empty());
        when(tagRepository.findByName(anyString())).thenReturn(Optional.of(testTag));
        when(translationRepository.save(any(Translation.class))).thenReturn(testTranslation);

        // Act
        TranslationResponseDTO result = translationService.createTranslation(testRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testTranslation.getId(), result.getId());
        assertEquals(testTranslation.getKey(), result.getKey());
        assertEquals(testTranslation.getContent(), result.getContent());
        assertEquals(testLocale.getCode(), result.getLocaleCode());
        assertEquals(testLocale.getName(), result.getLocaleName());
        assertEquals(1, result.getTags().size());
        assertTrue(result.getTags().contains(testTag.getName()));

        verify(localeRepository).findByCode(testRequestDTO.getLocaleCode());
        verify(translationRepository).findByKeyAndLocale(testRequestDTO.getKey(), testLocale);
        verify(tagRepository).findByName(testRequestDTO.getTags().get(0));
        verify(translationRepository).save(any(Translation.class));
    }

    @Test
    void createTranslation_WithTagsNotNull_AddsTagsToTranslation() {
        // Arrange
        when(localeRepository.findByCode(anyString())).thenReturn(Optional.of(testLocale));
        when(translationRepository.findByKeyAndLocale(anyString(), any(Locale.class))).thenReturn(Optional.empty());
        when(tagRepository.findByName(anyString())).thenReturn(Optional.of(testTag));
        when(translationRepository.save(any(Translation.class))).thenReturn(testTranslation);

        // Act
        TranslationResponseDTO result = translationService.createTranslation(testRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTags().size());
        assertTrue(result.getTags().contains(testTag.getName()));

        verify(tagRepository).findByName(testRequestDTO.getTags().get(0));
    }

    @Test
    void createTranslation_WithTagsNull_DoesNotAddTags() {
        // Arrange
        testRequestDTO.setTags(null);

        Translation translationWithoutTags = Translation.builder()
                .id(1L)
                .key("test.key")
                .content("Test content")
                .locale(testLocale)
                .tags(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(localeRepository.findByCode(anyString())).thenReturn(Optional.of(testLocale));
        when(translationRepository.findByKeyAndLocale(anyString(), any(Locale.class))).thenReturn(Optional.empty());
        when(translationRepository.save(any(Translation.class))).thenReturn(translationWithoutTags);

        // Act
        TranslationResponseDTO result = translationService.createTranslation(testRequestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getTags().isEmpty());

        verify(tagRepository, never()).findByName(anyString());
    }

    @Test
    void createTranslation_WithTagsEmpty_DoesNotAddTags() {
        // Arrange
        testRequestDTO.setTags(Collections.emptyList());

        Translation translationWithoutTags = Translation.builder()
                .id(1L)
                .key("test.key")
                .content("Test content")
                .locale(testLocale)
                .tags(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(localeRepository.findByCode(anyString())).thenReturn(Optional.of(testLocale));
        when(translationRepository.findByKeyAndLocale(anyString(), any(Locale.class))).thenReturn(Optional.empty());
        when(translationRepository.save(any(Translation.class))).thenReturn(translationWithoutTags);

        // Act
        TranslationResponseDTO result = translationService.createTranslation(testRequestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getTags().isEmpty());

        verify(tagRepository, never()).findByName(anyString());
    }

    @Test
    void createTranslation_WithNonExistingTag_CreatesNewTag() {
        // Arrange
        when(localeRepository.findByCode(anyString())).thenReturn(Optional.of(testLocale));
        when(translationRepository.findByKeyAndLocale(anyString(), any(Locale.class))).thenReturn(Optional.empty());
        when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(testTag);
        when(translationRepository.save(any(Translation.class))).thenReturn(testTranslation);

        // Act
        TranslationResponseDTO result = translationService.createTranslation(testRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTags().size());
        assertTrue(result.getTags().contains(testTag.getName()));

        verify(tagRepository).findByName(testRequestDTO.getTags().get(0));
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void createTranslation_WithExistingTranslation_ThrowsIllegalArgumentException() {
        // Arrange
        when(localeRepository.findByCode(anyString())).thenReturn(Optional.of(testLocale));
        when(translationRepository.findByKeyAndLocale(anyString(), any(Locale.class))).thenReturn(Optional.of(testTranslation));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> translationService.createTranslation(testRequestDTO));

        assertTrue(exception.getMessage().contains("Translation already exists"));
        verify(translationRepository, never()).save(any(Translation.class));
    }

    @Test
    void createTranslation_WithNonExistingLocale_ThrowsResourceNotFoundException() {
        // Arrange
        when(localeRepository.findByCode(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> translationService.createTranslation(testRequestDTO));

        assertTrue(exception.getMessage().contains("Locale not found"));
        verify(translationRepository, never()).save(any(Translation.class));
    }

    @Test
    void updateTranslation_WithValidData_ReturnsUpdatedTranslationResponseDTO() {
        // Arrange
        when(translationRepository.findById(anyLong())).thenReturn(Optional.of(testTranslation));
        when(localeRepository.findByCode(anyString())).thenReturn(Optional.of(testLocale));
        when(tagRepository.findByName(anyString())).thenReturn(Optional.of(testTag));
        when(translationRepository.save(any(Translation.class))).thenReturn(testTranslation);

        // Act
        TranslationResponseDTO result = translationService.updateTranslation(1L, testRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testTranslation.getId(), result.getId());
        assertEquals(testRequestDTO.getKey(), result.getKey());
        assertEquals(testRequestDTO.getContent(), result.getContent());
        assertEquals(testLocale.getCode(), result.getLocaleCode());

        verify(translationRepository).findById(1L);
        verify(localeRepository).findByCode(testRequestDTO.getLocaleCode());
        verify(translationRepository).save(any(Translation.class));
    }

    @Test
    void updateTranslation_WithTagsNotNull_UpdatesTags() {
        // Arrange
        when(translationRepository.findById(anyLong())).thenReturn(Optional.of(testTranslation));
        when(localeRepository.findByCode(anyString())).thenReturn(Optional.of(testLocale));

        // Set up a new tag for the request
        List<String> newTags = Collections.singletonList("new-tag");
        testRequestDTO.setTags(newTags);

        Tag newTag = new Tag();
        newTag.setId(2L);
        newTag.setName("new-tag");

        when(tagRepository.findByName("new-tag")).thenReturn(Optional.of(newTag));

        Set<Tag> updatedTags = new HashSet<>();
        updatedTags.add(newTag);

        Translation updatedTranslation = Translation.builder()
                .id(1L)
                .key("test.key")
                .content("Test content")
                .locale(testLocale)
                .tags(updatedTags)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(translationRepository.save(any(Translation.class))).thenReturn(updatedTranslation);

        // Act
        TranslationResponseDTO result = translationService.updateTranslation(1L, testRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTags().size());
        assertTrue(result.getTags().contains("new-tag"));

        verify(tagRepository).findByName("new-tag");
    }

    @Test
    void updateTranslation_WithTagsNull_ClearsTags() {
        // Arrange
        when(translationRepository.findById(anyLong())).thenReturn(Optional.of(testTranslation));
        when(localeRepository.findByCode(anyString())).thenReturn(Optional.of(testLocale));
        testRequestDTO.setTags(null);

        Translation updatedTranslation = Translation.builder()
                .id(1L)
                .key("test.key")
                .content("Test content")
                .locale(testLocale)
                .tags(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(translationRepository.save(any(Translation.class))).thenReturn(updatedTranslation);

        // Act
        TranslationResponseDTO result = translationService.updateTranslation(1L, testRequestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getTags().isEmpty());

        verify(tagRepository, never()).findByName(anyString());
    }

    @Test
    void updateTranslation_WithTagsEmpty_ClearsTags() {
        // Arrange
        when(translationRepository.findById(anyLong())).thenReturn(Optional.of(testTranslation));
        when(localeRepository.findByCode(anyString())).thenReturn(Optional.of(testLocale));
        testRequestDTO.setTags(Collections.emptyList());

        Translation updatedTranslation = Translation.builder()
                .id(1L)
                .key("test.key")
                .content("Test content")
                .locale(testLocale)
                .tags(new HashSet<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(translationRepository.save(any(Translation.class))).thenReturn(updatedTranslation);

        // Act
        TranslationResponseDTO result = translationService.updateTranslation(1L, testRequestDTO);

        // Assert
        assertNotNull(result);
        assertTrue(result.getTags().isEmpty());

        verify(tagRepository, never()).findByName(anyString());
    }

    @Test
    void updateTranslation_WithNonExistingTranslation_ThrowsResourceNotFoundException() {
        // Arrange
        when(translationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> translationService.updateTranslation(1L, testRequestDTO));

        assertTrue(exception.getMessage().contains("Translation not found"));
        verify(translationRepository, never()).save(any(Translation.class));
    }

    @Test
    void updateTranslation_WithNonExistingLocale_ThrowsResourceNotFoundException() {
        // Arrange
        when(translationRepository.findById(anyLong())).thenReturn(Optional.of(testTranslation));
        when(localeRepository.findByCode(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> translationService.updateTranslation(1L, testRequestDTO));

        assertTrue(exception.getMessage().contains("Locale not found"));
        verify(translationRepository, never()).save(any(Translation.class));
    }

    @Test
    void getTranslation_WithExistingId_ReturnsTranslationResponseDTO() {
        // Arrange
        when(translationRepository.findById(anyLong())).thenReturn(Optional.of(testTranslation));

        // Act
        TranslationResponseDTO result = translationService.getTranslation(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testTranslation.getId(), result.getId());
        assertEquals(testTranslation.getKey(), result.getKey());
        assertEquals(testTranslation.getContent(), result.getContent());

        verify(translationRepository).findById(1L);
    }

    @Test
    void getTranslation_WithNonExistingId_ThrowsResourceNotFoundException() {
        // Arrange
        when(translationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> translationService.getTranslation(1L));

        assertTrue(exception.getMessage().contains("Translation not found"));
    }

    @Test
    void getAllTranslations_ReturnsListOfTranslationResponseDTOs() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Translation> page = new PageImpl<>(Collections.singletonList(testTranslation));
        when(translationRepository.findAll(pageable)).thenReturn(page);

        // Act
        List<TranslationResponseDTO> results = translationService.getAllTranslations(pageable);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testTranslation.getId(), results.get(0).getId());

        verify(translationRepository).findAll(pageable);
    }

    @Test
    void getTranslationsByLocale_WithExistingLocale_ReturnsMapOfTranslations() {
        // Arrange
        when(localeRepository.findByCode(anyString())).thenReturn(Optional.of(testLocale));
        when(translationRepository.findByLocale(any(Locale.class))).thenReturn(Collections.singletonList(testTranslation));

        // Act
        Map<String, String> results = translationService.getTranslationsByLocale("en-US");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testTranslation.getContent(), results.get(testTranslation.getKey()));

        verify(localeRepository).findByCode("en-US");
        verify(translationRepository).findByLocale(testLocale);
    }

    @Test
    void getTranslationsByLocale_WithNonExistingLocale_ThrowsResourceNotFoundException() {
        // Arrange
        when(localeRepository.findByCode(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> translationService.getTranslationsByLocale("non-existent"));

        assertTrue(exception.getMessage().contains("Locale not found"));
        verify(translationRepository, never()).findByLocale(any(Locale.class));
    }

    @Test
    void getAllTranslationsJson_ReturnsMapOfTranslationsByLocale() {
        // Arrange
        List<Locale> locales = Collections.singletonList(testLocale);
        when(localeRepository.findAll()).thenReturn(locales);
        when(localeRepository.findByCode(anyString())).thenReturn(Optional.of(testLocale));
        when(translationRepository.findByLocale(any(Locale.class))).thenReturn(Collections.singletonList(testTranslation));

        // Act
        Map<String, Map<String, String>> results = translationService.getAllTranslationsJson();

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertTrue(results.containsKey(testLocale.getCode()));
        assertEquals(1, results.get(testLocale.getCode()).size());

        verify(localeRepository).findAll();
        verify(localeRepository).findByCode(testLocale.getCode());
        verify(translationRepository).findByLocale(testLocale);
    }

    @Test
    void searchTranslations_WithAllParamsProvided_ReturnsPageOfTranslationResponseDTOs() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Translation> page = new PageImpl<>(Collections.singletonList(testTranslation));

        when(translationRepository.searchTranslations(
                anyString(), anyString(), anyString(), anyList(), anyInt(), any(Pageable.class)
        )).thenReturn(page);

        // Act
        Page<TranslationResponseDTO> results = translationService.searchTranslations(testSearchRequestDTO);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.getTotalElements());

        verify(translationRepository).searchTranslations(
                testSearchRequestDTO.getKey(),
                testSearchRequestDTO.getContent(),
                testSearchRequestDTO.getLocaleCode(),
                testSearchRequestDTO.getTags(),
                testSearchRequestDTO.getTags().size(),
                pageable
        );
    }

    @Test
    void searchTranslations_WithNullPage_UsesDefaultPage() {
        // Arrange
        testSearchRequestDTO.setPage(null);
        Page<Translation> page = new PageImpl<>(Collections.singletonList(testTranslation));

        when(translationRepository.searchTranslations(
                anyString(), anyString(), anyString(), anyList(), anyInt(), any(Pageable.class)
        )).thenReturn(page);

        // Act
        Page<TranslationResponseDTO> results = translationService.searchTranslations(testSearchRequestDTO);

        // Assert
        assertNotNull(results);

        // Verify that PageRequest was created with default page 0
        verify(translationRepository).searchTranslations(
                any(), any(), any(), any(), any(),
                argThat(pageable -> pageable.getPageNumber() == 0)
        );
    }

    @Test
    void searchTranslations_WithNullSize_UsesDefaultSize() {
        // Arrange
        testSearchRequestDTO.setSize(null);
        Page<Translation> page = new PageImpl<>(Collections.singletonList(testTranslation));

        when(translationRepository.searchTranslations(
                anyString(), anyString(), anyString(), anyList(), anyInt(), any(Pageable.class)
        )).thenReturn(page);

        // Act
        Page<TranslationResponseDTO> results = translationService.searchTranslations(testSearchRequestDTO);

        // Assert
        assertNotNull(results);

        // Verify that PageRequest was created with default size 20
        verify(translationRepository).searchTranslations(
                any(), any(), any(), any(), any(),
                argThat(pageable -> pageable.getPageSize() == 20)
        );
    }

    @Test
    void searchTranslations_WithNullTags_UsesEmptyList() {
        // Arrange
        testSearchRequestDTO.setTags(null);
        Page<Translation> page = new PageImpl<>(Collections.singletonList(testTranslation));

        when(translationRepository.searchTranslations(
                anyString(), anyString(), anyString(), anyList(), anyInt(), any(Pageable.class)
        )).thenReturn(page);

        // Act
        Page<TranslationResponseDTO> results = translationService.searchTranslations(testSearchRequestDTO);

        // Assert
        assertNotNull(results);

        // Verify that an empty list was used for tags
        verify(translationRepository).searchTranslations(
                any(), any(), any(),
                argThat(List::isEmpty),
                eq(0),
                any(Pageable.class)
        );
    }

    @Test
    void getTranslationsByTagName_ReturnsListOfTranslationResponseDTOs() {
        // Arrange
        when(translationRepository.findByTagName(anyString())).thenReturn(Collections.singletonList(testTranslation));

        // Act
        List<TranslationResponseDTO> results = translationService.getTranslationsByTagName("test-tag");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testTranslation.getId(), results.get(0).getId());

        verify(translationRepository).findByTagName("test-tag");
    }

    @Test
    void getTranslationsByKeyPattern_ReturnsListOfTranslationResponseDTOs() {
        // Arrange
        when(translationRepository.findByKeyContaining(anyString())).thenReturn(Collections.singletonList(testTranslation));

        // Act
        List<TranslationResponseDTO> results = translationService.getTranslationsByKeyPattern("test");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testTranslation.getId(), results.get(0).getId());

        verify(translationRepository).findByKeyContaining("test");
    }

    @Test
    void getTranslationsByContentPattern_ReturnsListOfTranslationResponseDTOs() {
        // Arrange
        when(translationRepository.findByContentContaining(anyString())).thenReturn(Collections.singletonList(testTranslation));

        // Act
        List<TranslationResponseDTO> results = translationService.getTranslationsByContentPattern("content");

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testTranslation.getId(), results.get(0).getId());

        verify(translationRepository).findByContentContaining("content");
    }

    @Test
    void deleteTranslation_WithExistingId_DeletesTranslation() {
        // Arrange
        when(translationRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(translationRepository).deleteById(anyLong());

        // Act
        translationService.deleteTranslation(1L);

        // Assert
        verify(translationRepository).existsById(1L);
        verify(translationRepository).deleteById(1L);
    }

    @Test
    void deleteTranslation_WithNonExistingId_ThrowsResourceNotFoundException() {
        // Arrange
        when(translationRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> translationService.deleteTranslation(1L));

        assertTrue(exception.getMessage().contains("Translation not found"));
        verify(translationRepository, never()).deleteById(anyLong());
    }
}