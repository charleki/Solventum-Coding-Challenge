package com.charleskim.shortlink.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public class DecodeRequest {

    @NotBlank
    @URL
    private String shortUrl;

    public String getShortUrl() {
        return shortUrl;
    }
}
