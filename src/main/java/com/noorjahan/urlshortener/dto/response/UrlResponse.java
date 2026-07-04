package com.noorjahan.urlshortener.dto.response;

public class UrlResponse {

    private String originalUrl;
    private String shortCode;
    private String ShortUrl;

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortUrl() {
        return ShortUrl;
    }

    public void setShortUrl(String shortUrl) {
        ShortUrl = shortUrl;
    }

}
