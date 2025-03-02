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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TranslationService {

    private final TranslationRepository translationRepository;
    private final LocaleRepository localeRepository;
    private final TagRepository tagRepository;

    public TranslationService(TranslationRepository translationRepository, LocaleRepository localeRepository, TagRepository tagRepository) {
        this.translationRepository = translationRepository;
        this.localeRepository = localeRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    @CacheEvict(value = {"translations", "translationsByLocale"}, allEntries = true)
    public TranslationResponseDTO createTranslation(TranslationRequestDTO requestDTO) {
        Locale locale = localeRepository.findByCode(requestDTO.getLocaleCode())
                .orElseThrow(() -> new ResourceNotFoundException("Locale not found with code: " + requestDTO.getLocaleCode()));

        // Check if translation already exists
        Optional<Translation> existingTranslation = translationRepository.findByKeyAndLocale(requestDTO.getKey(), locale);
        if (existingTranslation.isPresent()) {
            throw new IllegalArgumentException("Translation already exists for key: " + requestDTO.getKey() + " and locale: " + requestDTO.getLocaleCode());
        }

        Translation translation = Translation.builder()
                .key(requestDTO.getKey())
                .content(requestDTO.getContent())
                .locale(locale)
                .tags(new HashSet<>())
                .build();


        if (requestDTO.getTags() != null && !requestDTO.getTags().isEmpty()) {
            Set<Tag> tags = getOrCreateTags(requestDTO.getTags());
            tags.forEach(translation::addTag);
        }

        Translation savedTranslation = translationRepository.save(translation);
        return mapToDTO(savedTranslation);
    }

    @Transactional
    @CacheEvict(value = {"translations", "translationsByLocale"}, allEntries = true)
    public TranslationResponseDTO updateTranslation(Long id, TranslationRequestDTO requestDTO) {
        Translation translation = translationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found with id: " + id));

        Locale locale = localeRepository.findByCode(requestDTO.getLocaleCode())
                .orElseThrow(() -> new ResourceNotFoundException("Locale not found with code: " + requestDTO.getLocaleCode()));

        translation.setKey(requestDTO.getKey());
        translation.setContent(requestDTO.getContent());
        translation.setLocale(locale);

        // Update tags
        translation.getTags().clear();
        if (requestDTO.getTags() != null && !requestDTO.getTags().isEmpty()) {
            Set<Tag> tags = getOrCreateTags(requestDTO.getTags());
            tags.forEach(translation::addTag);
        }

        Translation updatedTranslation = translationRepository.save(translation);
        return mapToDTO(updatedTranslation);
    }

    @Transactional(readOnly = true)
    public TranslationResponseDTO getTranslation(Long id) {
        Translation translation = translationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found with id: " + id));
        return mapToDTO(translation);
    }

    @Transactional(readOnly = true)
    @Cacheable("translations")
    public List<TranslationResponseDTO> getAllTranslations(Pageable pageable) {
        return translationRepository.findAll(pageable).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "translationsByLocale", key = "#localeCode")
    public Map<String, String> getTranslationsByLocale(String localeCode) {
        Locale locale = localeRepository.findByCode(localeCode)
                .orElseThrow(() -> new ResourceNotFoundException("Locale not found with code: " + localeCode));

        List<Translation> translations = translationRepository.findByLocale(locale);
        Map<String, String> translationMap = new HashMap<>();

        for (Translation translation : translations) {
            translationMap.put(translation.getKey(), translation.getContent());
        }

        return translationMap;
    }

    @Transactional(readOnly = true)
    @Cacheable("allTranslationsJson")
    public Map<String, Map<String, String>> getAllTranslationsJson() {
        List<Locale> locales = localeRepository.findAll();
        Map<String, Map<String, String>> result = new HashMap<>();

        for (Locale locale : locales) {
            Map<String, String> translations = getTranslationsByLocale(locale.getCode());
            result.put(locale.getCode(), translations);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Page<TranslationResponseDTO> searchTranslations(SearchRequestDTO searchRequest) {
        int page = searchRequest.getPage() != null ? searchRequest.getPage() : 0;
        int size = searchRequest.getSize() != null ? searchRequest.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);

        List<String> tagNames = searchRequest.getTags() != null ? searchRequest.getTags() : new ArrayList<>();
        Integer tagCount = tagNames.isEmpty() ? 0 : tagNames.size();

        Page<Translation> translations = translationRepository.searchTranslations(
                searchRequest.getKey(),
                searchRequest.getContent(),
                searchRequest.getLocaleCode(),
                tagNames,
                tagCount,
                pageable
        );

        return translations.map(this::mapToDTO);
    }

    @Transactional(readOnly = true)
    public List<TranslationResponseDTO> getTranslationsByTagName(String tagName) {
        return translationRepository.findByTagName(tagName).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TranslationResponseDTO> getTranslationsByKeyPattern(String keyPattern) {
        return translationRepository.findByKeyContaining(keyPattern).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TranslationResponseDTO> getTranslationsByContentPattern(String contentPattern) {
        return translationRepository.findByContentContaining(contentPattern).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = {"translations", "translationsByLocale", "allTranslationsJson"}, allEntries = true)
    public void deleteTranslation(Long id) {
        if (!translationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Translation not found with id: " + id);
        }
        translationRepository.deleteById(id);
    }

    private Set<Tag> getOrCreateTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String tagName : tagNames) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        // Create a new tag if not found
                        Tag newTag = new Tag();
                        newTag.setName(tagName);
                        return tagRepository.save(newTag);
                    });
            tags.add(tag);
        }
        return tags;
    }

    private TranslationResponseDTO mapToDTO(Translation translation) {
        return TranslationResponseDTO.builder()
                .id(translation.getId())
                .key(translation.getKey())
                .content(translation.getContent())
                .localeCode(translation.getLocale().getCode())
                .localeName(translation.getLocale().getName())
                .tags(translation.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList()))
                .createdAt(translation.getCreatedAt())
                .updatedAt(translation.getUpdatedAt())
                .build();
    }
}