package com.noorjahan.urlshortener.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlResponse {

    private String originalUrl;
    private String shortCode;
    private String shortUrl;
}
