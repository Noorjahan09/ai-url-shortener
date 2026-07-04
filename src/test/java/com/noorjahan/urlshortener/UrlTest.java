package com.noorjahan.urlshortener.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UrlTest {

    @Test
    void testUrlBuilder() {
        LocalDateTime now = LocalDateTime.now();

        Url url = Url.builder()
                .id(1L)
                .originalUrl("https://www.example.com/long/path")
                .shortCode("abc123")
                .createdAt(now)
                .updatedAt(now)
                .clickCount(0L)
                .build();

        assertNotNull(url);
        assertEquals(1L, url.getId());
        assertEquals("https://www.example.com/long/path", url.getOriginalUrl());
        assertEquals("abc123", url.getShortCode());
        assertEquals(now, url.getCreatedAt());
        assertEquals(now, url.getUpdatedAt());
        assertEquals(0L, url.getClickCount());
    }

    @Test
    void testUrlGettersSetters() {
        Url url = new Url();
        url.setId(2L);
        url.setOriginalUrl("https://example.com");
        url.setShortCode("xyz789");
        url.setClickCount(5L);

        assertEquals(2L, url.getId());
        assertEquals("https://example.com", url.getOriginalUrl());
        assertEquals("xyz789", url.getShortCode());
        assertEquals(5L, url.getClickCount());
    }

    @Test
    void testUrlClickCountIncrement() {
        Url url = Url.builder()
                .originalUrl("https://example.com")
                .shortCode("abc123")
                .clickCount(0L)
                .build();

        assertEquals(0L, url.getClickCount());

        url.setClickCount(url.getClickCount() + 1);
        assertEquals(1L, url.getClickCount());

        url.setClickCount(url.getClickCount() + 1);
        assertEquals(2L, url.getClickCount());
    }

    @Test
    void testUrlImmutability_CreatedAtNotUpdatable() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2026, 1, 1, 1, 0, 0);

        Url url = Url.builder()
                .originalUrl("https://example.com")
                .shortCode("abc123")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(createdAt, url.getCreatedAt());
        assertEquals(updatedAt, url.getUpdatedAt());

        LocalDateTime newDate = LocalDateTime.of(2026, 7, 5, 12, 0, 0);
        url.setUpdatedAt(newDate);

        assertEquals(createdAt, url.getCreatedAt(), "CreatedAt should remain unchanged");
        assertEquals(newDate, url.getUpdatedAt(), "UpdatedAt should be updated");
    }

    @Test
    void testUrlNoArgsConstructor() {
        Url url = new Url();
        assertNotNull(url);
        assertNull(url.getId());
        assertNull(url.getOriginalUrl());
    }

    @Test
    void testUrlAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Url url = new Url(1L, "https://example.com", "abc123", now, now, 0L);

        assertEquals(1L, url.getId());
        assertEquals("https://example.com", url.getOriginalUrl());
        assertEquals("abc123", url.getShortCode());
        assertEquals(now, url.getCreatedAt());
        assertEquals(now, url.getUpdatedAt());
        assertEquals(0L, url.getClickCount());
    }
}
