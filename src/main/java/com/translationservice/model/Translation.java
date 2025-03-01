package com.translationservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "translations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"translation_key", "locale_id"})
        }
)
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "translation_key", nullable = false)
    private String key;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locale_id", nullable = false)
    private Locale locale;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "translation_tags",
            joinColumns = @JoinColumn(name = "translation_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Translation() {
    }

    public Translation(Long id, String key, String content, Locale locale, Set<Tag> tags, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.key = key;
        this.content = content;
        this.locale = locale;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Static builder method
    public static TranslationBuilder builder() {
        return new TranslationBuilder();
    }

    // Method to add tag
    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    // Method to remove tag
    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getTranslations().remove(this);
    }

    // Getters and setters
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

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
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
    public static class TranslationBuilder {
        private Long id;
        private String key;
        private String content;
        private Locale locale;
        private Set<Tag> tags = new HashSet<>();
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        TranslationBuilder() {
        }

        public TranslationBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public TranslationBuilder key(String key) {
            this.key = key;
            return this;
        }

        public TranslationBuilder content(String content) {
            this.content = content;
            return this;
        }

        public TranslationBuilder locale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public TranslationBuilder tags(Set<Tag> tags) {
            this.tags = tags;
            return this;
        }

        public TranslationBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public TranslationBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Translation build() {
            return new Translation(id, key, content, locale, tags, createdAt, updatedAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}