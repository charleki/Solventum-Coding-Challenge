package com.charleskim.shortlink.dto;

public class DecodeResponse {

    private final String originalUrl;

    public DecodeResponse(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }
}
