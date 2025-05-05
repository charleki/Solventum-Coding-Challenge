package com.charleskim.shortlink.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Semaphore;

@Configuration
@EnableConfigurationProperties(ConcurrencyProperties.class)
public class ConcurrencyConfig {

    @Bean
    public Semaphore encodeRequestSemaphore(ConcurrencyProperties concurrencyProperties) {
        return new Semaphore(concurrencyProperties.getEncode().requestLimit());
    }

    @Bean
    public Semaphore decodeRequestSemaphore(ConcurrencyProperties concurrencyProperties) {
        return new Semaphore(concurrencyProperties.getDecode().requestLimit());
    }
}
