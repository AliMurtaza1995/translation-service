package com.translationservice.repository;

import com.translationservice.model.Locale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocaleRepository extends JpaRepository<Locale, Long> {
    Optional<Locale> findByCode(String code);
    boolean existsByCode(String code);
}