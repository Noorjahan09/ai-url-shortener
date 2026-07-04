package com.noorjahan.urlshortener.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {

    private String originalUrl;
    private String shortCode;
    private Long clickCount;
    private LocalDateTime createdAt;
}
