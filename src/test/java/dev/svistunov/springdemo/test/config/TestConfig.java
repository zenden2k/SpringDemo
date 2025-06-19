package dev.svistunov.springdemo.test.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

@TestConfiguration
public class TestConfig {
/*
    @Bean
    @Primary
    public PageableHandlerMethodArgumentResolver pageableResolver() {
        return new PageableHandlerMethodArgumentResolver();
    }*/
}