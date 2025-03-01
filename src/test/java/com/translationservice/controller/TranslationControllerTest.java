package com.translationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.translationservice.dto.SearchRequestDTO;
import com.translationservice.dto.TranslationRequestDTO;
import com.translationservice.dto.TranslationResponseDTO;
import com.translationservice.exception.GlobalExceptionHandler;
import com.translationservice.exception.ResourceNotFoundException;
import com.translationservice.service.TranslationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.translationservice.dto.SearchRequestDTO;
import com.translationservice.dto.TranslationRequestDTO;
import com.translationservice.dto.TranslationResponseDTO;
import com.translationservice.service.TranslationService;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranslationControllerTest {

    @Mock
    private TranslationService translationService;

    @InjectMocks
    private TranslationController translationController;

    private TranslationRequestDTO requestDTO;
    private TranslationResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new TranslationRequestDTO();
        requestDTO.setKey("test.key");
        requestDTO.setLocaleCode("en");
        requestDTO.setContent("Test Content");

        responseDTO = new TranslationResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setKey("test.key");
        responseDTO.setLocaleCode("en");
        responseDTO.setContent("Test Content");
    }

    @Test
    void testCreateTranslation() {
        when(translationService.createTranslation(any(TranslationRequestDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<TranslationResponseDTO> response = translationController.createTranslation(requestDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(translationService).createTranslation(requestDTO);
    }

    @Test
    void testUpdateTranslation() {
        when(translationService.updateTranslation(eq(1L), any(TranslationRequestDTO.class)))
                .thenReturn(responseDTO);

        ResponseEntity<TranslationResponseDTO> response = translationController.updateTranslation(1L, requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(translationService).updateTranslation(1L, requestDTO);
    }

    @Test
    void testGetTranslation() {
        when(translationService.getTranslation(1L)).thenReturn(responseDTO);

        ResponseEntity<TranslationResponseDTO> response = translationController.getTranslation(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(translationService).getTranslation(1L);
    }

    @Test
    void testGetAllTranslations() {
        List<TranslationResponseDTO> translations = new ArrayList<>();
        translations.add(responseDTO);

        when(translationService.getAllTranslations(any(Pageable.class)))
                .thenReturn(translations);

        ResponseEntity<List<TranslationResponseDTO>> response = translationController.getAllTranslations(
                0, 20, "id", "asc"
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(translations, response.getBody());
        verify(translationService).getAllTranslations(any(Pageable.class));
    }

    @Test
    void testGetAllTranslationsWithDescSort() {
        List<TranslationResponseDTO> translations = new ArrayList<>();
        translations.add(responseDTO);

        when(translationService.getAllTranslations(any(Pageable.class)))
                .thenReturn(translations);

        ResponseEntity<List<TranslationResponseDTO>> response = translationController.getAllTranslations(
                0, 20, "id", "desc"
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(translations, response.getBody());
        verify(translationService).getAllTranslations(any(Pageable.class));
    }

    @Test
    void testGetTranslationsByLocale() {
        Map<String, String> translations = new HashMap<>();
        translations.put("test.key", "Test Content");

        when(translationService.getTranslationsByLocale("en"))
                .thenReturn(translations);

        ResponseEntity<Map<String, String>> response = translationController.getTranslationsByLocale("en");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(translations, response.getBody());
        verify(translationService).getTranslationsByLocale("en");
    }

    @Test
    void testGetAllTranslationsJson() {
        Map<String, Map<String, String>> allTranslations = new HashMap<>();
        Map<String, String> enTranslations = new HashMap<>();
        enTranslations.put("test.key", "Test Content");
        allTranslations.put("en", enTranslations);

        when(translationService.getAllTranslationsJson())
                .thenReturn(allTranslations);

        ResponseEntity<Map<String, Map<String, String>>> response = translationController.getAllTranslationsJson();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(allTranslations, response.getBody());
        verify(translationService).getAllTranslationsJson();
    }

    @Test
    void testSearchTranslations() {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        List<TranslationResponseDTO> content = new ArrayList<>();
        content.add(responseDTO);
        Page<TranslationResponseDTO> page = new PageImpl<>(content);

        when(translationService.searchTranslations(any(SearchRequestDTO.class)))
                .thenReturn(page);

        ResponseEntity<Page<TranslationResponseDTO>> response = translationController.searchTranslations(searchRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(page, response.getBody());
        verify(translationService).searchTranslations(searchRequest);
    }

    @Test
    void testGetTranslationsByTagName() {
        List<TranslationResponseDTO> translations = new ArrayList<>();
        translations.add(responseDTO);

        when(translationService.getTranslationsByTagName("testTag"))
                .thenReturn(translations);

        ResponseEntity<List<TranslationResponseDTO>> response = translationController.getTranslationsByTagName("testTag");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(translations, response.getBody());
        verify(translationService).getTranslationsByTagName("testTag");
    }

    @Test
    void testGetTranslationsByKeyPattern() {
        List<TranslationResponseDTO> translations = new ArrayList<>();
        translations.add(responseDTO);

        when(translationService.getTranslationsByKeyPattern("test.*"))
                .thenReturn(translations);

        ResponseEntity<List<TranslationResponseDTO>> response = translationController.getTranslationsByKeyPattern("test.*");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(translations, response.getBody());
        verify(translationService).getTranslationsByKeyPattern("test.*");
    }

    @Test
    void testGetTranslationsByContentPattern() {
        List<TranslationResponseDTO> translations = new ArrayList<>();
        translations.add(responseDTO);

        when(translationService.getTranslationsByContentPattern("*Content*"))
                .thenReturn(translations);

        ResponseEntity<List<TranslationResponseDTO>> response = translationController.getTranslationsByContentPattern("*Content*");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(translations, response.getBody());
        verify(translationService).getTranslationsByContentPattern("*Content*");
    }

    @Test
    void testDeleteTranslation() {
        doNothing().when(translationService).deleteTranslation(1L);

        ResponseEntity<Void> response = translationController.deleteTranslation(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(translationService).deleteTranslation(1L);
    }

    // Validation test for PageRequest
    @Test
    void testPageRequestCreation() {
        // Verify page and size are handled correctly
        Pageable pageable = PageRequest.of(
                0,
                20,
                Sort.by(Sort.Direction.ASC, "id")
        );

        assertEquals(0, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
        assertEquals("id: ASC", pageable.getSort().toString());
    }
}