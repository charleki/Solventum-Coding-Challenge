package com.charleskim.shortlink.controller;

import com.charleskim.shortlink.service.UrlShortenerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlShortenerController.class)
class UrlShortenerControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UrlShortenerService mockUrlShortenerService;
    @MockitoBean(name = "encodeRequestSemaphore")
    private Semaphore mockEncodeRequestSemaphore;
    @MockitoBean(name = "decodeRequestSemaphore")
    private Semaphore mockDecodeRequestSemaphore;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        when(mockUrlShortenerService.encodeUrl("https://example.com/test"))
                .thenReturn("http://short.est/abc123");
        when(mockUrlShortenerService.decodeUrl("http://short.est/abc123"))
                .thenReturn("https://example.com/test");
    }

    @Test
    void testEncodeEndpoint() throws Exception {
        when(mockEncodeRequestSemaphore.tryAcquire(0, TimeUnit.SECONDS))
                .thenReturn(true);

        mockMvc.perform(post("/encode")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("originalUrl", "https://example.com/test"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value("http://short.est/abc123"));
    }

    @Test
    void returnsTooManyRequestsStatus_whenCannotAcquireSemaphorePermitForEncode() throws Exception {
        when(mockEncodeRequestSemaphore.tryAcquire(0, TimeUnit.SECONDS))
                .thenReturn(false);

        mockMvc.perform(post("/encode")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("originalUrl", "https://example.com/test"))))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void testDecodeEndpoint() throws Exception {
        when(mockDecodeRequestSemaphore.tryAcquire(0, TimeUnit.SECONDS))
                .thenReturn(true);

        mockMvc.perform(post("/decode")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("shortUrl", "http://short.est/abc123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://example.com/test"));
    }

    @Test
    void returnsTooManyRequestsStatus_whenCannotAcquireSemaphorePermitForDecode() throws Exception {
        when(mockDecodeRequestSemaphore.tryAcquire(0, TimeUnit.SECONDS))
                .thenReturn(false);

        mockMvc.perform(post("/decode")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("shortUrl", "http://short.est/abc123"))))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void returnsBadRequestStatus_whenInvalidUrlIsReceived() throws Exception {
        when(mockDecodeRequestSemaphore.tryAcquire(0, TimeUnit.SECONDS))
                .thenReturn(true);

        mockMvc.perform(post("/decode")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(Map.of("shortUrl", "http://invalid.com"))))
                .andExpect(status().isBadRequest());
    }
}