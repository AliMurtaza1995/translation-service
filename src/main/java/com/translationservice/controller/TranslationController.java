package com.translationservice.controller;

import com.translationservice.dto.SearchRequestDTO;
import com.translationservice.dto.TranslationRequestDTO;
import com.translationservice.dto.TranslationResponseDTO;
import com.translationservice.service.TranslationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/translations")
@SecurityRequirement(name = "bearerToken")
public class TranslationController {

    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping
    public ResponseEntity<TranslationResponseDTO> createTranslation(@Valid @RequestBody TranslationRequestDTO requestDTO) {
        return new ResponseEntity<>(translationService.createTranslation(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TranslationResponseDTO> updateTranslation(@PathVariable Long id, @Valid @RequestBody TranslationRequestDTO requestDTO) {
        return ResponseEntity.ok(translationService.updateTranslation(id, requestDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TranslationResponseDTO> getTranslation(@PathVariable Long id) {
        return ResponseEntity.ok(translationService.getTranslation(id));
    }

    @GetMapping
    public ResponseEntity<List<TranslationResponseDTO>> getAllTranslations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)
        );
        return ResponseEntity.ok(translationService.getAllTranslations(pageable));
    }

    @GetMapping("/locale/{localeCode}")
    public ResponseEntity<Map<String, String>> getTranslationsByLocale(@PathVariable String localeCode) {
        return ResponseEntity.ok(translationService.getTranslationsByLocale(localeCode));
    }

    @GetMapping("/export")
    public ResponseEntity<Map<String, Map<String, String>>> getAllTranslationsJson() {
        return ResponseEntity.ok(translationService.getAllTranslationsJson());
    }

    @PostMapping("/search")
    public ResponseEntity<Page<TranslationResponseDTO>> searchTranslations(@RequestBody SearchRequestDTO searchRequest) {
        return ResponseEntity.ok(translationService.searchTranslations(searchRequest));
    }

    @GetMapping("/tag/{tagName}")
    public ResponseEntity<List<TranslationResponseDTO>> getTranslationsByTagName(@PathVariable String tagName) {
        return ResponseEntity.ok(translationService.getTranslationsByTagName(tagName));
    }

    @GetMapping("/key/{keyPattern}")
    public ResponseEntity<List<TranslationResponseDTO>> getTranslationsByKeyPattern(@PathVariable String keyPattern) {
        return ResponseEntity.ok(translationService.getTranslationsByKeyPattern(keyPattern));
    }

    @GetMapping("/content/{contentPattern}")
    public ResponseEntity<List<TranslationResponseDTO>> getTranslationsByContentPattern(@PathVariable String contentPattern) {
        return ResponseEntity.ok(translationService.getTranslationsByContentPattern(contentPattern));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTranslation(@PathVariable Long id) {
        translationService.deleteTranslation(id);
        return ResponseEntity.noContent().build();
    }
}