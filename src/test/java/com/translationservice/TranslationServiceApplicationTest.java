package com.translationservice;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class TranslationServiceApplicationTest {

    @Test
    void main_ShouldStartSpringApplication() {
        // Use mockito to mock the static method SpringApplication.run
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            // Arrange
            mocked.when(() -> SpringApplication.run(
                    eq(TranslationServiceApplication.class),
                    any(String[].class))
            ).thenReturn(null);

            // Act
            TranslationServiceApplication.main(new String[]{});

            // Assert
            mocked.verify(() -> SpringApplication.run(
                            eq(TranslationServiceApplication.class),
                            any(String[].class)),
                    Mockito.times(1)
            );
        }
    }
    @Test
    void testConstructor() {
        // Simply instantiate the class
        TranslationServiceApplication application = new TranslationServiceApplication();
        // No assertion needed; we're just testing the constructor can be called
        assertNotNull(application);
    }
}