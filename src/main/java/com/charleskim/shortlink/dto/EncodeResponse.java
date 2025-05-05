package com.charleskim.shortlink.dto;

public class EncodeResponse {

    private final String shortUrl;

    public EncodeResponse(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }
}
