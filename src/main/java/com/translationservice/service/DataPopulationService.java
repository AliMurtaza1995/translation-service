package com.translationservice.service;

import com.translationservice.model.Locale;
import com.translationservice.model.Tag;
import com.translationservice.model.Translation;
import com.translationservice.repository.LocaleRepository;
import com.translationservice.repository.TagRepository;
import com.translationservice.repository.TranslationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Profile("!test")
public class DataPopulationService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataPopulationService.class);

    private final TranslationRepository translationRepository;
    private final LocaleRepository localeRepository;
    private final TagRepository tagRepository;
    private final TransactionTemplate transactionTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.data.populate:false}")
    private boolean populateData;

    @Value("${app.data.count:100000}")
    private int dataCount;

    private static final String[] TAG_NAMES = {
            "mobile", "desktop", "web", "admin", "user", "public", "private",
            "login", "signup", "dashboard", "settings", "error"
    };

    // Same SAMPLE_KEYS and TAG_NAMES arrays

    public DataPopulationService(TranslationRepository translationRepository,
                                 LocaleRepository localeRepository,
                                 TagRepository tagRepository,
                                 PlatformTransactionManager transactionManager) {
        this.translationRepository = translationRepository;
        this.localeRepository = localeRepository;
        this.tagRepository = tagRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public void run(String... args) {
        if (populateData) {
            generateTestData(dataCount);
        }
    }

    public void generateTestData(int count) {
        log.info("Starting data population with {} records", count);
        long startTime = System.currentTimeMillis();

        // Clear existing translations
        transactionTemplate.execute(status -> {
            log.info("Clearing existing translations...");
            translationRepository.deleteAll();
            log.info("Translations cleared");
            return null;
        });

        // Ensure locales exist
        List<Locale> locales = transactionTemplate.execute(status -> {
            return ensureLocalesExist();
        });

        // Ensure tags exist
        List<Tag> tags = transactionTemplate.execute(status -> {
            return ensureTagsExist();
        });

        // Add translations in small batches
        int totalCreated = 0;
        int batchSize = 100;
        int targetPerLocale = count / locales.size();

        for (Locale locale : locales) {
            log.info("Creating translations for locale: {}", locale.getCode());

            for (int batch = 0; batch < Math.ceil(targetPerLocale / (double)batchSize); batch++) {
                final int batchNumber = batch;

                int batchCount = transactionTemplate.execute(status -> {
                    int created = 0;
                    for (int i = 0; i < batchSize && (batchNumber * batchSize + i) < targetPerLocale; i++) {
                        int index = batchNumber * batchSize + i;

                        // Create simple key and content
                        String key = "key." + locale.getCode() + "." + index;
                        String content = "Content for " + key;

                        // Create translation
                        Translation translation = new Translation();
                        translation.setKey(key);
                        translation.setContent(content);
                        translation.setLocale(locale);

                        // Save it without tags
                        translationRepository.save(translation);
                        created++;
                    }
                    return created;
                });

                totalCreated += batchCount;
                log.info("Created batch {} with {} translations for locale {}. Total: {}",
                        batch, batchCount, locale.getCode(), totalCreated);
            }
        }

        long endTime = System.currentTimeMillis();
        log.info("Data population completed. {} translations created in {} ms", totalCreated, endTime - startTime);
    }

    private List<Locale> ensureLocalesExist() {
        List<Locale> locales = localeRepository.findAll();

        if (locales.isEmpty()) {
            locales = Arrays.asList(
                    Locale.builder().code("en").name("English").build(),
                    Locale.builder().code("fr").name("French").build(),
                    Locale.builder().code("es").name("Spanish").build(),
                    Locale.builder().code("de").name("German").build(),
                    Locale.builder().code("it").name("Italian").build()
            );
            localeRepository.saveAll(locales);
        }

        return locales;
    }

    private List<Tag> ensureTagsExist() {
        List<Tag> existingTags = tagRepository.findAll();

        if (existingTags.isEmpty()) {
            List<Tag> tagsToCreate = Arrays.stream(TAG_NAMES)
                    .map(name -> {
                        Tag tag = new Tag();
                        tag.setName((String) name);
                        return tag;
                    })
                    .collect(Collectors.toList());
            existingTags = tagRepository.saveAll(tagsToCreate);
        }

        return existingTags;
    }
}