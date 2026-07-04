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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;

    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public UrlResponse createShortUrl(CreateShortURLRequest request) {
        Url existingUrl = urlRepository.findByOriginalUrl(request.getOrignalUrl())
                .orElse(null);
        if (existingUrl != null) {
            return mapToResponse(existingUrl);
        }
        String shortCode;
        do {
            shortCode = ShortCodeGenerator.generate();
        } while (urlRepository.existsByShortCode(shortCode));

        Url url = Url.builder()
                .originalUrl(request.getOrignalUrl())
                .shortCode(shortCode)
                .build();
        return mapToResponse(urlRepository.save(url));
    }

    @Override
    public UrlResponse getShortUrl(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found for code: " + shortCode));
        url.setClickCount(url.getClickCount() + 1);
        return mapToResponse(urlRepository.save(url));
    }

    @Override
    public AnalyticsResponse getAnalytics(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found for code: " + shortCode));
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
