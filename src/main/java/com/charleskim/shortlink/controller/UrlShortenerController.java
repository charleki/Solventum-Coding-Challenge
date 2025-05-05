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

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static com.charleskim.shortlink.constants.ShortLinkConstants.BASE_URL;

@RestController
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;
    private final Semaphore encodeRequestSemaphore;
    private final Semaphore decodeRequestSemaphore;

    public UrlShortenerController(UrlShortenerService urlShortenerService, Semaphore encodeRequestSemaphore,
                                  Semaphore decodeRequestSemaphore) {
        this.urlShortenerService = urlShortenerService;
        this.encodeRequestSemaphore = encodeRequestSemaphore;
        this.decodeRequestSemaphore = decodeRequestSemaphore;
    }

    @PostMapping("/encode")
    public ResponseEntity<EncodeResponse> encode(@Valid @RequestBody EncodeRequest encodeRequest) throws InterruptedException {
        if (!encodeRequestSemaphore.tryAcquire(0, TimeUnit.SECONDS)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        try {
            String shortUrl = urlShortenerService.encodeUrl(encodeRequest.getOriginalUrl());
            return ResponseEntity.ok(new EncodeResponse(shortUrl));
        } finally {
            encodeRequestSemaphore.release();
        }
    }

    @PostMapping("/decode")
    public ResponseEntity<DecodeResponse> decode(@Valid @RequestBody DecodeRequest decodeRequest) throws InterruptedException {
        if (!decodeRequestSemaphore.tryAcquire(0, TimeUnit.SECONDS)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        try {
            validateShortUrl(decodeRequest.getShortUrl());
            String originalUrl = urlShortenerService.decodeUrl(decodeRequest.getShortUrl());
            if (originalUrl == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(new DecodeResponse(originalUrl));
        } finally {
            decodeRequestSemaphore.release();
        }
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
