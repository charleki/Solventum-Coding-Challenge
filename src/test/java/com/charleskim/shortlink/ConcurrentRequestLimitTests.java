package com.charleskim.shortlink;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ConcurrentRequestLimitTests {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${shortlink.concurrency.encode.request-limit}")
    private int encodeRequestLimit;
    @Value("${shortlink.concurrency.decode.request-limit}")
    private int decodeRequestLimit;

    @Test
    void testTooManyConcurrentRequests() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(encodeRequestLimit + 2);
        List<Future<ResponseEntity<String>>> futures = new ArrayList<>();
        RestClient restClient = RestClient.create("http://localhost:8080");

        for (int i = 0; i < encodeRequestLimit + 2; i++) {
            futures.add(executor.submit(() -> {
                try {
                    return restClient.post()
                            .uri("/encode")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(objectMapper.writeValueAsString(Map.of("originalUrl", "http://www.example.com/test")))
                            .retrieve()
                            .toEntity(String.class);
                } catch (HttpClientErrorException e) {
                    return ResponseEntity.status(e.getStatusCode()).build();
                }
            }));
        }

        int tooManyRequestsCount = 0;
        for (Future<ResponseEntity<String>> future : futures) {
            ResponseEntity<String> response = future.get();
            if (response.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)) {
                tooManyRequestsCount++;
            }
        }

        assertThat(tooManyRequestsCount).isEqualTo(2);
        executor.shutdown();
    }
}
