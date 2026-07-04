package com.noorjahan.urlshortener.dto;

import com.noorjahan.urlshortener.dto.request.CreateShortURLRequest;
import com.noorjahan.urlshortener.dto.response.AnalyticsResponse;
import com.noorjahan.urlshortener.dto.response.UrlResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testCreateShortURLRequest_ValidHttps() {
        CreateShortURLRequest request = new CreateShortURLRequest();
        request.setOrignalUrl("https://www.example.com/path");

        Set<ConstraintViolation<CreateShortURLRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Valid HTTPS URL should pass validation");
    }

    @Test
    void testCreateShortURLRequest_ValidHttp() {
        CreateShortURLRequest request = new CreateShortURLRequest();
        request.setOrignalUrl("http://www.example.com/path");

        Set<ConstraintViolation<CreateShortURLRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Valid HTTP URL should pass validation");
    }

    @Test
    void testCreateShortURLRequest_InvalidProtocol() {
        CreateShortURLRequest request = new CreateShortURLRequest();
        request.setOrignalUrl("ftp://www.example.com/path");

        Set<ConstraintViolation<CreateShortURLRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "FTP URL should fail validation");
    }

    @Test
    void testCreateShortURLRequest_NoProtocol() {
        CreateShortURLRequest request = new CreateShortURLRequest();
        request.setOrignalUrl("www.example.com/path");

        Set<ConstraintViolation<CreateShortURLRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "URL without protocol should fail validation");
    }

    @Test
    void testCreateShortURLRequest_EmptyUrl() {
        CreateShortURLRequest request = new CreateShortURLRequest();
        request.setOrignalUrl("");

        Set<ConstraintViolation<CreateShortURLRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Empty URL should fail validation");
    }

    @Test
    void testCreateShortURLRequest_NullUrl() {
        CreateShortURLRequest request = new CreateShortURLRequest();
        request.setOrignalUrl(null);

        Set<ConstraintViolation<CreateShortURLRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(), "Null URL should fail validation");
    }

    @Test
    void testUrlResponse_BuilderPattern() {
        UrlResponse response = UrlResponse.builder()
                .originalUrl("https://example.com")
                .shortCode("abc123")
                .shortUrl("http://short.url/abc123")
                .build();

        assertNotNull(response);
        assertEquals("https://example.com", response.getOriginalUrl());
        assertEquals("abc123", response.getShortCode());
        assertEquals("http://short.url/abc123", response.getShortUrl());
    }

    @Test
    void testAnalyticsResponse_BuilderPattern() {
        LocalDateTime now = LocalDateTime.now();
        AnalyticsResponse response = AnalyticsResponse.builder()
                .originalUrl("https://example.com")
                .shortCode("abc123")
                .clickCount(10L)
                .createdAt(now)
                .build();

        assertNotNull(response);
        assertEquals("https://example.com", response.getOriginalUrl());
        assertEquals("abc123", response.getShortCode());
        assertEquals(10L, response.getClickCount());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    void testUrlResponse_GetterSetter() {
        UrlResponse response = new UrlResponse();
        response.setOriginalUrl("https://example.com");
        response.setShortCode("xyz789");
        response.setShortUrl("http://short.url/xyz789");

        assertEquals("https://example.com", response.getOriginalUrl());
        assertEquals("xyz789", response.getShortCode());
        assertEquals("http://short.url/xyz789", response.getShortUrl());
    }

    @Test
    void testAnalyticsResponse_GetterSetter() {
        AnalyticsResponse response = new AnalyticsResponse();
        LocalDateTime now = LocalDateTime.now();

        response.setOriginalUrl("https://example.com");
        response.setShortCode("abc123");
        response.setClickCount(5L);
        response.setCreatedAt(now);

        assertEquals("https://example.com", response.getOriginalUrl());
        assertEquals("abc123", response.getShortCode());
        assertEquals(5L, response.getClickCount());
        assertEquals(now, response.getCreatedAt());
    }
}
