package com.noorjahan.urlshortener.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CreateShortURLRequest {

    @NotBlank(message = "Original URL is required")
    private String orignalUrl;

    public String getOrignalUrl() {
        return orignalUrl;
    }

    public void setOrignalUrl(String orignalUrl) {
        this.orignalUrl = orignalUrl;
    }
}
