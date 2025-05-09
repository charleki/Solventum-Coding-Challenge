package com.charleskim.shortlink.config;

import com.charleskim.shortlink.interceptor.ConcurrencyLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ConcurrencyLimitInterceptor concurrencyLimitInterceptor;

    public WebConfig(ConcurrencyLimitInterceptor concurrencyLimitInterceptor) {
        this.concurrencyLimitInterceptor = concurrencyLimitInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(concurrencyLimitInterceptor)
                .addPathPatterns("/encode", "/decode");
    }
}
