package com.translationservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import static org.junit.jupiter.api.Assertions.*;

class CacheConfigTest {

    @Test
    void testCacheManagerConfiguration() {
        // Create cache config
        CacheConfig cacheConfig = new CacheConfig();

        // Get cache manager
        CacheManager cacheManager = cacheConfig.cacheManager();

        // Verify cache manager type
        assertTrue(cacheManager instanceof ConcurrentMapCacheManager);

        // Verify cache names
        String[] expectedCacheNames = {"translations", "translationsByLocale", "allTranslationsJson"};

        for (String cacheName : expectedCacheNames) {
            assertNotNull(cacheManager.getCache(cacheName),
                    "Cache " + cacheName + " should be configured");
        }
    }

    @Test
    void testCacheConfigAnnotations() {
        // Verify @Configuration annotation
        assertNotNull(CacheConfig.class.getAnnotation(org.springframework.context.annotation.Configuration.class));

        // Verify @EnableCaching annotation
        assertNotNull(CacheConfig.class.getAnnotation(org.springframework.cache.annotation.EnableCaching.class));
    }
}