package com.noorjahan.urlshortener.service;

import com.noorjahan.urlshortener.dto.request.CreateShortURLRequest;
import com.noorjahan.urlshortener.dto.response.AnalyticsResponse;
import com.noorjahan.urlshortener.dto.response.UrlResponse;
import com.noorjahan.urlshortener.entity.Url;
import com.noorjahan.urlshortener.exception.UrlNotFoundException;
import com.noorjahan.urlshortener.repository.UrlRepository;
import com.noorjahan.urlshortener.service.impl.UrlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlServiceImpl urlService;

    private CreateShortURLRequest createRequest;
    private Url mockUrl;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlService, "baseUrl", "http://localhost:8080");

        createRequest = new CreateShortURLRequest();
        createRequest.setOrignalUrl("https://www.example.com/very/long/url");

        mockUrl = Url.builder()
                .id(1L)
                .originalUrl("https://www.example.com/very/long/url")
                .shortCode("abc123")
                .clickCount(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateShortUrl_Success() {
        when(urlRepository.findByOriginalUrl(createRequest.getOrignalUrl()))
                .thenReturn(Optional.empty());
        when(urlRepository.existsByShortCode(any(String.class)))
                .thenReturn(false);
        when(urlRepository.save(any(Url.class)))
                .thenReturn(mockUrl);

        UrlResponse response = urlService.createShortUrl(createRequest);

        assertNotNull(response);
        assertEquals("https://www.example.com/very/long/url", response.getOriginalUrl());
        assertEquals("abc123", response.getShortCode());
        assertEquals("http://localhost:8080/abc123", response.getShortUrl());

        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    void testCreateShortUrl_UrlAlreadyExists() {
        when(urlRepository.findByOriginalUrl(createRequest.getOrignalUrl()))
                .thenReturn(Optional.of(mockUrl));

        UrlResponse response = urlService.createShortUrl(createRequest);

        assertNotNull(response);
        assertEquals("abc123", response.getShortCode());

        verify(urlRepository, never()).save(any(Url.class));
    }

    @Test
    void testGetShortUrl_Success() {
        when(urlRepository.findByShortCode("abc123"))
                .thenReturn(Optional.of(mockUrl));
        when(urlRepository.save(any(Url.class)))
                .thenReturn(mockUrl);

        UrlResponse response = urlService.getShortUrl("abc123");

        assertNotNull(response);
        assertEquals("https://www.example.com/very/long/url", response.getOriginalUrl());
        assertEquals("abc123", response.getShortCode());

        verify(urlRepository, times(1)).save(any(Url.class));
    }

    @Test
    void testGetShortUrl_NotFound() {
        when(urlRepository.findByShortCode("xyz999"))
                .thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> {
            urlService.getShortUrl("xyz999");
        });

        verify(urlRepository, never()).save(any(Url.class));
    }

    @Test
    void testGetAnalytics_Success() {
        Url urlWithClicks = Url.builder()
                .id(1L)
                .originalUrl("https://www.example.com/very/long/url")
                .shortCode("abc123")
                .clickCount(5L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(urlRepository.findByShortCode("abc123"))
                .thenReturn(Optional.of(urlWithClicks));

        AnalyticsResponse response = urlService.getAnalytics("abc123");

        assertNotNull(response);
        assertEquals("https://www.example.com/very/long/url", response.getOriginalUrl());
        assertEquals("abc123", response.getShortCode());
        assertEquals(5L, response.getClickCount());

        verify(urlRepository, times(1)).findByShortCode("abc123");
    }

    @Test
    void testGetAnalytics_NotFound() {
        when(urlRepository.findByShortCode("invalid"))
                .thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> {
            urlService.getAnalytics("invalid");
        });
    }
}
