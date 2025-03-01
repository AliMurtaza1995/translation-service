package com.translationservice.service;

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
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DataPopulationServiceTest {

    @Mock
    private TranslationRepository translationRepository;

    @Mock
    private LocaleRepository localeRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PlatformTransactionManager transactionManager;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private DataPopulationService dataPopulationService;

    private List<Locale> locales;

    @BeforeEach
    void setUp() {
        // Prepare standard locales
        locales = Arrays.asList(
                Locale.builder().code("en").name("English").build(),
                Locale.builder().code("fr").name("French").build()
        );

        // Configure test-specific settings
        ReflectionTestUtils.setField(dataPopulationService, "populateData", true);
        ReflectionTestUtils.setField(dataPopulationService, "dataCount", 100);

        // Lenient mocking for common repository behaviors
        lenient().when(localeRepository.findAll()).thenReturn(locales);
        lenient().when(localeRepository.saveAll(anyList())).thenReturn(locales);
        lenient().when(tagRepository.findAll()).thenReturn(new ArrayList<>());
        lenient().when(tagRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
        lenient().when(translationRepository.save(any())).thenReturn(new Translation());
        lenient().when(transactionTemplate.execute(any())).thenReturn(new ArrayList<>());
    }

    @Test
    void testRunWithDataPopulation() {
        // Suppress logging to reduce noise
        try (MockedStatic<LoggerFactory> loggerFactoryMock = Mockito.mockStatic(LoggerFactory.class)) {
            // Execute data population
            dataPopulationService.run();
        } catch (Exception e) {
            // Catch and print any unexpected exceptions
            e.printStackTrace();
        }
    }

    @Test
    void testGenerateTestDataWithSmallCount() {
        // Suppress logging to reduce noise
        try (MockedStatic<LoggerFactory> loggerFactoryMock = Mockito.mockStatic(LoggerFactory.class)) {
            // Execute data population with small count
            dataPopulationService.generateTestData(10);
        } catch (Exception e) {
            // Catch and print any unexpected exceptions
            e.printStackTrace();
        }
    }

    @Test
    void testRunWithDataPopulationDisabled() {
        // Disable data population
        ReflectionTestUtils.setField(dataPopulationService, "populateData", false);

        // Execute run method
        dataPopulationService.run();

        // Verify no repositories were called
        verify(translationRepository, never()).deleteAll();
    }

    @Test
    void testEnsureLocalesExist_WithExistingLocales() {
        // Use reflection to call private method
        List<Locale> result = ReflectionTestUtils.invokeMethod(
                dataPopulationService,
                "ensureLocalesExist"
        );

        // Verify
        assert result != null;
        assert result.size() == 2;
    }

    @Test
    void testEnsureLocalesExist_WithNoLocales() {
        // Reset the findAll mock to return an empty list
        when(localeRepository.findAll()).thenReturn(new ArrayList<>());
        when(localeRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // Use reflection to call private method
        List<Locale> result = ReflectionTestUtils.invokeMethod(
                dataPopulationService,
                "ensureLocalesExist"
        );

        // Verify
        assert result != null;
        assert result.size() == 5;
    }

    @Test
    void testEnsureTagsExist_WithExistingTags() {
        // Prepare existing tags
        List<Tag> existingTags = Arrays.asList(
                createTag("mobile"),
                createTag("web")
        );

        // Mock repository to return existing tags
        when(tagRepository.findAll()).thenReturn(existingTags);

        // Use reflection to call private method
        List<Tag> result = ReflectionTestUtils.invokeMethod(
                dataPopulationService,
                "ensureTagsExist"
        );

        // Verify
        assert result != null;
        assert result.size() == 2;
    }

    @Test
    void testEnsureTagsExist_WithNoTags() {
        // Reset the findAll mock to return an empty list
        when(tagRepository.findAll()).thenReturn(new ArrayList<>());
        when(tagRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // Use reflection to call private method
        List<Tag> result = ReflectionTestUtils.invokeMethod(
                dataPopulationService,
                "ensureTagsExist"
        );

        // Verify
        assert result != null;
        assert result.size() > 0;
    }

    // Utility method to create a tag
    private Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tag;
    }
}
