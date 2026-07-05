package com.noorjahan.urlshortener.controller;

import com.noorjahan.urlshortener.dto.request.CreateShortURLRequest;
import com.noorjahan.urlshortener.dto.response.AnalyticsResponse;
import com.noorjahan.urlshortener.dto.response.UrlResponse;
import com.noorjahan.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * URL Controller - REST API endpoints for URL shortener service.
 *
 * <p>Provides HTTP endpoints for creating, retrieving, and analyzing shortened URLs.
 * All endpoints validate input and handle errors through {@link GlobalExceptionHandler}.</p>
 *
 * <p>Base Path: {@code /api/v1/urls}</p>
 *
 * @author Noorjahan09
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
@Slf4j
public class UrlController {

    private final UrlService urlService;

    /**
     * Creates a new shortened URL.
     *
     * @param request the request containing the original URL
     * @return {@link ResponseEntity} with status 201 CREATED and {@link UrlResponse}
     * @throws IllegalArgumentException if URL is invalid (no http/https protocol)
     */
    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody CreateShortURLRequest request) {
        log.info("POST /api/v1/urls - Creating short URL for: {}", request.getOrignalUrl());
        UrlResponse response = urlService.createShortUrl(request);
        log.info("Successfully created short URL: {}", response.getShortCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves the original URL by short code and increments click counter.
     *
     * @param shortCode the unique short code identifier
     * @return {@link ResponseEntity} with status 200 OK and {@link UrlResponse}
     * @throws UrlNotFoundException if short code does not exist
     */
    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlResponse> getShortUrl(@PathVariable String shortCode) {
        log.info("GET /api/v1/urls/{} - Retrieving short URL", shortCode);
        UrlResponse response = urlService.getShortUrl(shortCode);
        log.info("Short URL retrieved successfully: {}", response.getOriginalUrl());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves analytics for a shortened URL.
     *
     * @param shortCode the unique short code identifier
     * @return {@link ResponseEntity} with status 200 OK and {@link AnalyticsResponse}
     * @throws UrlNotFoundException if short code does not exist
     */
    @GetMapping("/{shortCode}/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalytics(@PathVariable String shortCode) {
        log.info("GET /api/v1/urls/{}/analytics - Fetching analytics", shortCode);
        AnalyticsResponse response = urlService.getAnalytics(shortCode);
        log.info("Analytics retrieved - Clicks: {}", response.getClickCount());
        return ResponseEntity.ok(response);
    }
}
