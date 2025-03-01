package com.translationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class TranslationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TranslationService translationService;

    @InjectMocks
    private TranslationController translationController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(translationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateTranslation() throws Exception {
        TranslationRequestDTO requestDTO = createSampleTranslationRequestDTO();
        TranslationResponseDTO responseDTO = createSampleTranslationResponseDTO();

        when(translationService.createTranslation(any(TranslationRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/translations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDTO.getId()))
                .andExpect(jsonPath("$.key").value(responseDTO.getKey()));

        verify(translationService).createTranslation(any(TranslationRequestDTO.class));
    }

    @Test
    public void testUpdateTranslation() throws Exception {
        Long translationId = 1L;
        TranslationRequestDTO requestDTO = createSampleTranslationRequestDTO();
        TranslationResponseDTO responseDTO = createSampleTranslationResponseDTO();

        when(translationService.updateTranslation(eq(translationId), any(TranslationRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(put("/api/translations/{id}", translationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDTO.getId()));

        verify(translationService).updateTranslation(eq(translationId), any(TranslationRequestDTO.class));
    }

    @Test
    public void testGetTranslation() throws Exception {
        Long translationId = 1L;
        TranslationResponseDTO responseDTO = createSampleTranslationResponseDTO();

        when(translationService.getTranslation(translationId))
                .thenReturn(responseDTO);

        mockMvc.perform(get("/api/translations/{id}", translationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDTO.getId()));

        verify(translationService).getTranslation(translationId);
    }

    @Test
    public void testGetAllTranslations() throws Exception {
        List<TranslationResponseDTO> translations = Arrays.asList(
                createSampleTranslationResponseDTO(),
                createSampleTranslationResponseDTO()
        );

        when(translationService.getAllTranslations(any(Pageable.class)))
                .thenReturn(translations);

        mockMvc.perform(get("/api/translations")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sortBy", "id")
                        .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(translationService).getAllTranslations(any(Pageable.class));
    }

    @Test
    public void testGetTranslationsByLocale() throws Exception {
        String localeCode = "en";
        Map<String, String> translations = new HashMap<>();
        translations.put("key1", "Translation 1");
        translations.put("key2", "Translation 2");

        when(translationService.getTranslationsByLocale(localeCode))
                .thenReturn(translations);

        mockMvc.perform(get("/api/translations/locale/{localeCode}", localeCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key1").value("Translation 1"))
                .andExpect(jsonPath("$.key2").value("Translation 2"));

        verify(translationService).getTranslationsByLocale(localeCode);
    }

    @Test
    public void testGetAllTranslationsJson() throws Exception {
        Map<String, Map<String, String>> jsonTranslations = new HashMap<>();
        Map<String, String> enTranslations = new HashMap<>();
        enTranslations.put("key1", "Translation 1");
        jsonTranslations.put("en", enTranslations);

        when(translationService.getAllTranslationsJson())
                .thenReturn(jsonTranslations);

        mockMvc.perform(get("/api/translations/export"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.en.key1").value("Translation 1"));

        verify(translationService).getAllTranslationsJson();
    }

    @Test
    public void testSearchTranslations() throws Exception {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setKey("test");

        List<TranslationResponseDTO> translations = Arrays.asList(
                createSampleTranslationResponseDTO(),
                createSampleTranslationResponseDTO()
        );
        Page<TranslationResponseDTO> pagedTranslations = new PageImpl<>(translations);

        when(translationService.searchTranslations(any(SearchRequestDTO.class)))
                .thenReturn(pagedTranslations);

        mockMvc.perform(post("/api/translations/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

        verify(translationService).searchTranslations(any(SearchRequestDTO.class));
    }

    @Test
    public void testGetTranslationsByTagName() throws Exception {
        String tagName = "mobile";
        List<TranslationResponseDTO> translations = Arrays.asList(
                createSampleTranslationResponseDTO(),
                createSampleTranslationResponseDTO()
        );

        when(translationService.getTranslationsByTagName(tagName))
                .thenReturn(translations);

        mockMvc.perform(get("/api/translations/tag/{tagName}", tagName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(translationService).getTranslationsByTagName(tagName);
    }

    @Test
    public void testDeleteTranslation() throws Exception {
        Long translationId = 1L;
        doNothing().when(translationService).deleteTranslation(translationId);

        mockMvc.perform(delete("/api/translations/{id}", translationId))
                .andExpect(status().isNoContent());

        verify(translationService).deleteTranslation(translationId);
    }

    // Helper methods to create sample DTOs
    private TranslationRequestDTO createSampleTranslationRequestDTO() {
        TranslationRequestDTO dto = new TranslationRequestDTO();
        dto.setKey("test.key");
        dto.setContent("Test Translation");
        dto.setLocaleCode("en");
        dto.setTags(List.of("mobile"));
        return dto;
    }

    private TranslationResponseDTO createSampleTranslationResponseDTO() {
        TranslationResponseDTO dto = new TranslationResponseDTO();
        dto.setId(1L);
        dto.setKey("test.key");
        dto.setContent("Test Translation");
        dto.setLocaleCode("en");
        dto.setTags(List.of("mobile"));
        return dto;
    }
}