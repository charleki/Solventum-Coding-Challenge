package com.charleskim.shortlink.controller;

import com.charleskim.shortlink.dto.DecodeRequest;
import com.charleskim.shortlink.dto.DecodeResponse;
import com.charleskim.shortlink.dto.EncodeRequest;
import com.charleskim.shortlink.dto.EncodeResponse;
import com.charleskim.shortlink.service.UrlShortenerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.charleskim.shortlink.constants.ShortLinkConstants.BASE_URL;

@RestController
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/encode")
    public ResponseEntity<EncodeResponse> encode(@Valid @RequestBody EncodeRequest encodeRequest) throws InterruptedException {
        String shortUrl = urlShortenerService.encodeUrl(encodeRequest.getOriginalUrl());

        return ResponseEntity.ok(new EncodeResponse(shortUrl));
    }

    @PostMapping("/decode")
    public ResponseEntity<DecodeResponse> decode(@Valid @RequestBody DecodeRequest decodeRequest) throws InterruptedException {
        validateShortUrl(decodeRequest.getShortUrl());
        String originalUrl = urlShortenerService.decodeUrl(decodeRequest.getShortUrl());
        if (originalUrl == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(new DecodeResponse(originalUrl));
    }

    private void validateShortUrl(String shortUrl) {
        if (!shortUrl.startsWith(BASE_URL)) {
            throw new IllegalArgumentException("Invalid short URL.");
        }
        String shortKey = shortUrl.substring((BASE_URL.length()));
        if (shortKey.isEmpty()) {
            throw new IllegalArgumentException("Missing short key in URL.");
        }
    }
}
