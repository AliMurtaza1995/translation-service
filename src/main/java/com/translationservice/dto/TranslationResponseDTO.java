package com.translationservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TranslationResponseDTO {
    private Long id;
    private String key;
    private String content;
    private String localeCode;
    private String localeName;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TranslationResponseDTO() {
    }

    public TranslationResponseDTO(Long id, String key, String content, String localeCode,
                                  String localeName, List<String> tags,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.key = key;
        this.content = content;
        this.localeCode = localeCode;
        this.localeName = localeName;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TranslationResponseDTOBuilder builder() {
        return new TranslationResponseDTOBuilder();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Builder class
    public static class TranslationResponseDTOBuilder {
        private Long id;
        private String key;
        private String content;
        private String localeCode;
        private String localeName;
        private List<String> tags;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        TranslationResponseDTOBuilder() {
        }

        public TranslationResponseDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TranslationResponseDTOBuilder key(String key) {
            this.key = key;
            return this;
        }

        public TranslationResponseDTOBuilder content(String content) {
            this.content = content;
            return this;
        }

        public TranslationResponseDTOBuilder localeCode(String localeCode) {
            this.localeCode = localeCode;
            return this;
        }

        public TranslationResponseDTOBuilder localeName(String localeName) {
            this.localeName = localeName;
            return this;
        }

        public TranslationResponseDTOBuilder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public TranslationResponseDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TranslationResponseDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public TranslationResponseDTO build() {
            return new TranslationResponseDTO(id, key, content, localeCode, localeName, tags, createdAt, updatedAt);
        }
    }
}