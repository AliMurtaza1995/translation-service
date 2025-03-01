package com.translationservice.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    public void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void testResourceNotFoundException() {

        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");


        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleResourceNotFoundException(ex);


        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Resource not found", body.get("message"));
        assertEquals(404, body.get("status"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    public void testResourceNotFoundExceptionConstructor() {

        ResourceNotFoundException ex = new ResourceNotFoundException("Test message");


        assertEquals("Test message", ex.getMessage());
    }

    @Test
    public void testIllegalArgumentException() {

        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");


        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgumentException(ex);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Invalid argument", body.get("message"));
        assertEquals(400, body.get("status"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    public void testMethodArgumentNotValidException() {

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("objectName", "fieldName", "error message");
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));


        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationExceptions(ex);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertNotNull(errors);
        assertEquals("error message", errors.get("fieldName"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    public void testGeneralException() {

        Exception ex = new Exception("Unexpected error");


        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGeneralException(ex);


        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("An unexpected error occurred", body.get("message"));
        assertEquals("Unexpected error", body.get("error"));
        assertEquals(500, body.get("status"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    public void testGeneralExceptionWithNullMessage() {

        Exception ex = new Exception() {
            @Override
            public String getMessage() {
                return null;
            }
        };


        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGeneralException(ex);


        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("An unexpected error occurred", body.get("message"));
        assertNull(body.get("error"));
        assertEquals(500, body.get("status"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    public void testMethodArgumentNotValidExceptionWithMultipleErrors() {

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        List<FieldError> errors = Arrays.asList(
                new FieldError("objectName", "field1", "error1"),
                new FieldError("objectName", "field2", "error2")
        );

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn((List) errors);


        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationExceptions(ex);


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);

        @SuppressWarnings("unchecked")
        Map<String, String> errorMap = (Map<String, String>) body.get("errors");
        assertNotNull(errorMap);
        assertEquals("error1", errorMap.get("field1"));
        assertEquals("error2", errorMap.get("field2"));
    }
}