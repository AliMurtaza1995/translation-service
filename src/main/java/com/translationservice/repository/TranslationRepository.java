package com.translationservice.repository;

import com.translationservice.model.Locale;
import com.translationservice.model.Translation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {

    Optional<Translation> findByKeyAndLocale(String key, Locale locale);

    List<Translation> findByLocale(Locale locale);

    @Query("SELECT t FROM Translation t JOIN t.tags tag WHERE tag.name = :tagName")
    List<Translation> findByTagName(@Param("tagName") String tagName);

    @Query("SELECT t FROM Translation t JOIN t.tags tag WHERE tag.name IN :tagNames")
    List<Translation> findByTagNames(@Param("tagNames") List<String> tagNames);

    @Query("SELECT t FROM Translation t JOIN t.locale l WHERE l.code = :localeCode")
    List<Translation> findByLocaleCode(@Param("localeCode") String localeCode);

    @Query("SELECT t FROM Translation t JOIN t.locale l JOIN t.tags tag WHERE l.code = :localeCode AND tag.name IN :tagNames")
    List<Translation> findByLocaleCodeAndTagNames(@Param("localeCode") String localeCode, @Param("tagNames") List<String> tagNames);

    @Query("SELECT t FROM Translation t WHERE t.key LIKE %:keyPattern%")
    List<Translation> findByKeyContaining(@Param("keyPattern") String keyPattern);

    @Query("SELECT t FROM Translation t WHERE t.content LIKE %:contentPattern%")
    List<Translation> findByContentContaining(@Param("contentPattern") String contentPattern);

    @Query(value = "SELECT t.* FROM translations t " +
            "JOIN locales l ON t.locale_id = l.id " +
            "LEFT JOIN translation_tags tt ON t.id = tt.translation_id " +
            "LEFT JOIN tags tag ON tt.tag_id = tag.id " +
            "WHERE (:key IS NULL OR t.translation_key LIKE CONCAT('%', :key, '%')) " +
            "AND (:content IS NULL OR t.content LIKE CONCAT('%', :content, '%')) " +
            "AND (:localeCode IS NULL OR l.code = :localeCode) " +
            "GROUP BY t.id " +
            "HAVING (:tagCount IS NULL OR :tagCount = 0 OR COUNT(DISTINCT CASE WHEN tag.name IN :tagNames THEN tag.id ELSE NULL END) = :tagCount)",
            countQuery = "SELECT COUNT(DISTINCT t.id) FROM translations t " +
                    "JOIN locales l ON t.locale_id = l.id " +
                    "LEFT JOIN translation_tags tt ON t.id = tt.translation_id " +
                    "LEFT JOIN tags tag ON tt.tag_id = tag.id " +
                    "WHERE (:key IS NULL OR t.translation_key LIKE CONCAT('%', :key, '%')) " +
                    "AND (:content IS NULL OR t.content LIKE CONCAT('%', :content, '%')) " +
                    "AND (:localeCode IS NULL OR l.code = :localeCode) " +
                    "GROUP BY t.id " +
                    "HAVING (:tagCount IS NULL OR :tagCount = 0 OR COUNT(DISTINCT CASE WHEN tag.name IN :tagNames THEN tag.id ELSE NULL END) = :tagCount)",
            nativeQuery = true)
    Page<Translation> searchTranslations(
            @Param("key") String key,
            @Param("content") String content,
            @Param("localeCode") String localeCode,
            @Param("tagNames") List<String> tagNames,
            @Param("tagCount") Integer tagCount,
            Pageable pageable);

    @Modifying
    @Query(value = "INSERT INTO translation_tags (translation_id, tag_id) VALUES (?1, ?2)", nativeQuery = true)
    void addTagToTranslation(Long translationId, Long tagId);
}