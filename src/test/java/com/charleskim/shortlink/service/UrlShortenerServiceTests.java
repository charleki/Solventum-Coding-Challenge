package com.charleskim.shortlink.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UrlShortenerServiceTests {

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    @Test
    void returnsSameCode_whenSameUrlEncodedTwice() {
        String originalUrl = "https://example.com/test";
        String shortUrl1 = urlShortenerService.encodeUrl(originalUrl);
        String shortUrl2 = urlShortenerService.encodeUrl(originalUrl);

        assertThat(shortUrl2).isEqualTo(shortUrl1);
    }

    @Test
    void returnsDifferentCodes_whenDifferentUrlsAreEncoded() {
        String shortUrl1 = urlShortenerService.encodeUrl("https://example.com/a");
        String shortUrl2 = urlShortenerService.encodeUrl("https://example.com/b");

        assertThat(shortUrl2).isNotEqualTo(shortUrl1);
    }

    @Test
    public void returnsOriginalUrl_whenEncodedAndThenDecoded() {
        String originalUrl = "https://example.com/test";
        String shortUrl = urlShortenerService.encodeUrl(originalUrl);
        String decodedUrl = urlShortenerService.decodeUrl(shortUrl);

        assertThat(decodedUrl).isEqualTo(originalUrl);
    }
}