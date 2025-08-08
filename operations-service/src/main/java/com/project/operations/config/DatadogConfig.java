package com.project.operations.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class DatadogConfig {

    @Autowired
    private MeterRegistry meterRegistry;

    @Bean
    public OncePerRequestFilter requestTimingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, 
                                          HttpServletResponse response, 
                                          FilterChain filterChain) throws ServletException, IOException {
                
                Timer.Sample sample = Timer.start(meterRegistry);
                try {
                    filterChain.doFilter(request, response);
                } finally {
                    sample.stop(Timer.builder("http.request.duration")
                            .tag("method", request.getMethod())
                            .tag("uri", request.getRequestURI())
                            .tag("status", String.valueOf(response.getStatus()))
                            .register(meterRegistry));
                }
            }
        };
    }
}