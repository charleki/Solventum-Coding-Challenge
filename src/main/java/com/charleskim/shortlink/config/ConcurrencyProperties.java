package com.charleskim.shortlink.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shortlink.concurrency")
public class ConcurrencyProperties {

    private final EndPointConcurrency encode;
    private final EndPointConcurrency decode;

    public ConcurrencyProperties(EndPointConcurrency encode, EndPointConcurrency decode) {
        this.encode = encode;
        this.decode = decode;
    }

    public EndPointConcurrency getEncode() {
        return encode;
    }

    public EndPointConcurrency getDecode() {
        return decode;
    }

    public record EndPointConcurrency(int requestLimit) {
    }
}
