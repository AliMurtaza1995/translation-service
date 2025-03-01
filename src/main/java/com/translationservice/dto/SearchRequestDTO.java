package com.translationservice.dto;

import java.util.List;

public class SearchRequestDTO {
    private String key;
    private String content;
    private String localeCode;
    private List<String> tags;
    private Integer page;
    private Integer size;

    public SearchRequestDTO() {
    }

    public SearchRequestDTO(String key, String content, String localeCode, List<String> tags, Integer page, Integer size) {
        this.key = key;
        this.content = content;
        this.localeCode = localeCode;
        this.tags = tags;
        this.page = page;
        this.size = size;
    }

    public static SearchRequestDTOBuilder builder() {
        return new SearchRequestDTOBuilder();
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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    // Builder class
    public static class SearchRequestDTOBuilder {
        private String key;
        private String content;
        private String localeCode;
        private List<String> tags;
        private Integer page;
        private Integer size;

        SearchRequestDTOBuilder() {
        }

        public SearchRequestDTOBuilder key(String key) {
            this.key = key;
            return this;
        }

        public SearchRequestDTOBuilder content(String content) {
            this.content = content;
            return this;
        }

        public SearchRequestDTOBuilder localeCode(String localeCode) {
            this.localeCode = localeCode;
            return this;
        }

        public SearchRequestDTOBuilder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public SearchRequestDTOBuilder page(Integer page) {
            this.page = page;
            return this;
        }

        public SearchRequestDTOBuilder size(Integer size) {
            this.size = size;
            return this;
        }

        public SearchRequestDTO build() {
            return new SearchRequestDTO(key, content, localeCode, tags, page, size);
        }
    }
}