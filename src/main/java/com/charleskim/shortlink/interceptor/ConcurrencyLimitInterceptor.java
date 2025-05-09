package com.charleskim.shortlink.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Component
public class ConcurrencyLimitInterceptor implements HandlerInterceptor {

    private final Semaphore encodeRequestSemaphore;
    private final Semaphore decodeRequestSemaphore;

    public ConcurrencyLimitInterceptor(Semaphore encodeRequestSemaphore, Semaphore decodeRequestSemaphore) {
        this.encodeRequestSemaphore = encodeRequestSemaphore;
        this.decodeRequestSemaphore = decodeRequestSemaphore;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException, InterruptedException {
        String path = request.getRequestURI();
        Semaphore semaphore = null;

        if ("/encode".equals(path)) {
            semaphore = encodeRequestSemaphore;
        } else if ("/decode".equals(path)) {
            semaphore = decodeRequestSemaphore;
        }
        if (semaphore != null && !semaphore.tryAcquire(0, TimeUnit.SECONDS)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many concurrent requests.");

            return false;
        }
        request.setAttribute("semaphore", semaphore);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Semaphore semaphore = (Semaphore) request.getAttribute("semaphore");

        if (semaphore != null) {
            semaphore.release();
        }
    }
}
