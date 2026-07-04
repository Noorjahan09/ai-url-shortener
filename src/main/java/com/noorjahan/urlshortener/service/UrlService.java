package com.noorjahan.urlshortener.service;

import com.noorjahan.urlshortener.dto.request.CreateShortURLRequest;
import com.noorjahan.urlshortener.dto.response.AnalyticsResponse;
import com.noorjahan.urlshortener.dto.response.UrlResponse;

public interface UrlService {

    UrlResponse createShortUrl(CreateShortURLRequest request);

    UrlResponse getShortUrl(String shortCode);

    AnalyticsResponse getAnalytics(String shortCode);
}
