package com.noorjahan.urlshortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noorjahan.urlshortener.controller.UrlController;
import com.noorjahan.urlshortener.dto.request.CreateShortURLRequest;
import com.noorjahan.urlshortener.dto.response.AnalyticsResponse;
import com.noorjahan.urlshortener.dto.response.UrlResponse;
import com.noorjahan.urlshortener.exception.UrlNotFoundException;
import com.noorjahan.urlshortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UrlService urlService;

    private CreateShortURLRequest createRequest;
    private UrlResponse urlResponse;
    private AnalyticsResponse analyticsResponse;

    @BeforeEach
    void setUp() {

        createRequest = new CreateShortURLRequest();
        createRequest.setOrignalUrl("https://www.example.com/very/long/url");

        urlResponse = UrlResponse.builder()
                .originalUrl("https://www.example.com/very/long/url")
                .shortCode("abc123")
                .shortUrl("http://localhost:8080/abc123")
                .build();

        analyticsResponse = AnalyticsResponse.builder()
                .originalUrl("https://www.example.com/very/long/url")
                .shortCode("abc123")
                .clickCount(5L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateShortUrl_Success() throws Exception {
        when(urlService.createShortUrl(any(CreateShortURLRequest.class)))
                .thenReturn(urlResponse);

        mockMvc.perform(post("/api/v1/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com/very/long/url"))
                .andExpect(jsonPath("$.shortCode").value("abc123"));

        verify(urlService, times(1)).createShortUrl(any(CreateShortURLRequest.class));
    }

    @Test
    void testCreateShortUrl_ValidationError_MissingUrl() throws Exception {
        CreateShortURLRequest invalidRequest = new CreateShortURLRequest();
        invalidRequest.setOrignalUrl("");

        mockMvc.perform(post("/api/v1/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"));

        verify(urlService, never()).createShortUrl(any(CreateShortURLRequest.class));
    }

    @Test
    void testCreateShortUrl_ValidationError_InvalidProtocol() throws Exception {
        CreateShortURLRequest invalidRequest = new CreateShortURLRequest();
        invalidRequest.setOrignalUrl("ftp://example.com");

        mockMvc.perform(post("/api/v1/urls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400));

        verify(urlService, never()).createShortUrl(any(CreateShortURLRequest.class));
    }

    @Test
    void testGetShortUrl_Success() throws Exception {
        when(urlService.getShortUrl("abc123"))
                .thenReturn(urlResponse);

        mockMvc.perform(get("/api/v1/urls/abc123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com/very/long/url"))
                .andExpect(jsonPath("$.shortCode").value("abc123"));

        verify(urlService, times(1)).getShortUrl("abc123");
    }

    @Test
    void testGetShortUrl_NotFound() throws Exception {
        when(urlService.getShortUrl("xyz999"))
                .thenThrow(new UrlNotFoundException("Short URL not found for code: xyz999"));

        mockMvc.perform(get("/api/v1/urls/xyz999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.error").value("URL_NOT_FOUND"));

        verify(urlService, times(1)).getShortUrl("xyz999");
    }

    @Test
    void testGetAnalytics_Success() throws Exception {
        when(urlService.getAnalytics("abc123"))
                .thenReturn(analyticsResponse);

        mockMvc.perform(get("/api/v1/urls/abc123/analytics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.clickCount").value(5));

        verify(urlService, times(1)).getAnalytics("abc123");
    }

    @Test
    void testGetAnalytics_NotFound() throws Exception {
        when(urlService.getAnalytics("invalid"))
                .thenThrow(new UrlNotFoundException("Short URL not found for code: invalid"));

        mockMvc.perform(get("/api/v1/urls/invalid/analytics")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404));

        verify(urlService, times(1)).getAnalytics("invalid");
    }
}
