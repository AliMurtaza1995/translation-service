package com.translationservice.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class TranslationRequestDTO {

    @NotBlank(message = "Translation key is required")
    private String key;

    @NotBlank(message = "Content is required")
    private String content;

    @NotBlank(message = "Locale code is required")
    private String localeCode;

    private List<String> tags;

    public TranslationRequestDTO() {
    }

    public TranslationRequestDTO(String key, String content, String localeCode, List<String> tags) {
        this.key = key;
        this.content = content;
        this.localeCode = localeCode;
        this.tags = tags;
    }

    public static TranslationRequestDTOBuilder builder() {
        return new TranslationRequestDTOBuilder();
    }

    // Getters and Setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    // Builder class
    public static class TranslationRequestDTOBuilder {
        private String key;
        private String content;
        private String localeCode;
        private List<String> tags;

        TranslationRequestDTOBuilder() {
        }

        public TranslationRequestDTOBuilder key(String key) {
            this.key = key;
            return this;
        }

        public TranslationRequestDTOBuilder content(String content) {
            this.content = content;
            return this;
        }

        public TranslationRequestDTOBuilder localeCode(String localeCode) {
            this.localeCode = localeCode;
            return this;
        }

        public TranslationRequestDTOBuilder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public TranslationRequestDTO build() {
            return new TranslationRequestDTO(key, content, localeCode, tags);
        }
    }
}