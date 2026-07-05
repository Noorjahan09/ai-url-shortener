package com.noorjahan.urlshortener.service;

import com.noorjahan.urlshortener.dto.request.CreateShortURLRequest;
import com.noorjahan.urlshortener.dto.response.AnalyticsResponse;
import com.noorjahan.urlshortener.dto.response.UrlResponse;

/**
 * URL Service Interface - Defines operations for URL shortening and management.
 *
 * <p>Provides contract for creating, retrieving, and analyzing shortened URLs.
 * All methods work with DTOs to maintain separation between service and persistence layers.</p>
 *
 * @author Noorjahan09
 * @version 1.0
 */
public interface UrlService {

    /**
     * Creates a new shortened URL or returns existing mapping if URL already shortened.
     *
     * @param request the request containing the original URL
     * @return {@link UrlResponse} containing original URL, short code, and computed short URL
     * @throws IllegalArgumentException if request or URL is invalid
     */
    UrlResponse createShortUrl(CreateShortURLRequest request);

    /**
     * Retrieves the original URL for a given short code and increments click count.
     *
     * @param shortCode the unique short code identifier
     * @return {@link UrlResponse} containing URL details
     * @throws UrlNotFoundException if short code does not exist
     */
    UrlResponse getShortUrl(String shortCode);

    /**
     * Retrieves analytics data for a shortened URL.
     *
     * @param shortCode the unique short code identifier
     * @return {@link AnalyticsResponse} containing access metrics and creation time
     * @throws UrlNotFoundException if short code does not exist
     */
    AnalyticsResponse getAnalytics(String shortCode);
}

