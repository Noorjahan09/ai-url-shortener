package com.noorjahan.urlshortener.service.impl;

import com.noorjahan.urlshortener.dto.request.CreateShortURLRequest;
import com.noorjahan.urlshortener.dto.response.AnalyticsResponse;
import com.noorjahan.urlshortener.dto.response.UrlResponse;
import com.noorjahan.urlshortener.entity.Url;
import com.noorjahan.urlshortener.exception.UrlNotFoundException;
import com.noorjahan.urlshortener.repository.UrlRepository;
import com.noorjahan.urlshortener.service.UrlService;
import com.noorjahan.urlshortener.util.ShortCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public UrlResponse createShortUrl(CreateShortURLRequest request) {
        log.debug("Creating short URL for original URL: {}", request.getOrignalUrl());
        
        Url existingUrl = urlRepository.findByOriginalUrl(request.getOrignalUrl())
                .orElse(null);
        if (existingUrl != null) {
            log.info("URL already shortened. Existing short code: {}", existingUrl.getShortCode());
            return mapToResponse(existingUrl);
        }
        
        String shortCode;
        do {
            shortCode = ShortCodeGenerator.generate();
        } while (urlRepository.existsByShortCode(shortCode));
        
        log.debug("Generated unique short code: {}", shortCode);

        Url url = Url.builder()
                .originalUrl(request.getOrignalUrl())
                .shortCode(shortCode)
                .build();
        Url savedUrl = urlRepository.save(url);
        log.info("Successfully created short URL. Short code: {}, Original URL: {}", shortCode, request.getOrignalUrl());
        
        return mapToResponse(savedUrl);
    }

    @Override
    public UrlResponse getShortUrl(String shortCode) {
        log.debug("Retrieving short URL for code: {}", shortCode);
        
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Short URL not found for code: {}", shortCode);
                    return new UrlNotFoundException("Short URL not found for code: " + shortCode);
                });
        
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
        log.info("Retrieved short URL. Code: {}, Click count: {}", shortCode, url.getClickCount());
        
        return mapToResponse(url);
    }

    @Override
    public AnalyticsResponse getAnalytics(String shortCode) {
        log.debug("Fetching analytics for short code: {}", shortCode);
        
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> {
                    log.warn("Analytics not found for code: {}", shortCode);
                    return new UrlNotFoundException("Short URL not found for code: " + shortCode);
                });
        
        log.info("Retrieved analytics. Code: {}, Total clicks: {}, Created at: {}", 
                shortCode, url.getClickCount(), url.getCreatedAt());
        
        return mapToAnalytics(url);
    }

    private UrlResponse mapToResponse(Url url) {
        return UrlResponse.builder()
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .shortUrl(baseUrl + "/" + url.getShortCode())
                .build();
    }

    private AnalyticsResponse mapToAnalytics(Url url) {
        return AnalyticsResponse.builder()
                .originalUrl(url.getOriginalUrl())
                .shortCode(url.getShortCode())
                .clickCount(url.getClickCount())
                .createdAt(url.getCreatedAt())
                .build();
    }
}

