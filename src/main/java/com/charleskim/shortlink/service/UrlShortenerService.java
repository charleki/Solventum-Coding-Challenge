package com.charleskim.shortlink.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.charleskim.shortlink.constants.ShortLinkConstants.BASE_URL;

@Service
public class UrlShortenerService {

    private static final int SHORT_KEY_BYTE_LENGTH = 6;

    private final Map<String, String> shortKeyToOriginalUrl = new ConcurrentHashMap<>();
    private final Map<String, String> originalUrlToShortKey = new ConcurrentHashMap<>();

    /**
     * Encodes a given original URL to a shortened URL.
     * If the URL has been previously encoded, the method will return the previously generated short URL.
     * If not, a new short key will be generated and associated with the original URL.
     *
     * @param originalUrl the original URL to be encoded into a short URL.
     * @return The shortened URL corresponding to the given original URL.
     */
    public String encodeUrl(String originalUrl) {
        if (originalUrlToShortKey.containsKey(originalUrl)) {
            return BASE_URL + originalUrlToShortKey.get(originalUrl);
        }
        String shortKey = generateShortKey();
        shortKeyToOriginalUrl.put(shortKey, originalUrl);
        originalUrlToShortKey.put(originalUrl, shortKey);

        return BASE_URL + shortKey;
    }

    /**
     * Decodes a shortened URL back to the original URL.
     * This method extracts the short key from the given short URL and looks up the corresponding original URL.
     *
     * @param shortUrl the shortened URL to be decoded.
     * @return The original URL corresponding to the provided short URL, or null if not found.
     */
    public String decodeUrl(String shortUrl) {
        String shortKey = extractShortKey(shortUrl);
        String originalUrl = shortKeyToOriginalUrl.get(shortKey);
        if (originalUrl == null) {
            return null;
        }

        String additionalParams = shortUrl.substring(BASE_URL.length() + shortKey.length());
        if (!additionalParams.isEmpty()) {
            return originalUrl + additionalParams;
        }

        return originalUrl;
    }

    /**
     * Generates a unique short key by creating 6 random bytes and encoding them with Base64 (URL-safe). Retries until a
     * unique key is found.
     *
     * @return A unique short key for use in shortened URLs.
     */
    private String generateShortKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[SHORT_KEY_BYTE_LENGTH];
        String shortKey;

        do {
            random.nextBytes(bytes);
            shortKey = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        } while (shortKeyToOriginalUrl.containsKey(shortKey));

        return shortKey;
    }

    private String extractShortKey(String shortUrl) {
        return shortUrl.substring(BASE_URL.length());
    }
}
