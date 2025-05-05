package com.charleskim.shortlink.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public class EncodeRequest {

    @NotBlank
    @URL
    private String originalUrl;

    public String getOriginalUrl() {
        return originalUrl;
    }
}
