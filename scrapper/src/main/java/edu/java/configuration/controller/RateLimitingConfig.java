package edu.java.configuration.controller;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitingConfig {

    private final ControllerConfig controllerConfig;

    public RateLimitingConfig(ControllerConfig controllerConfig) {
        this.controllerConfig = controllerConfig;
    }

    @Bean
    public Bucket TgChatRateLimitBucket() {
        ControllerConfig.TgChat config = controllerConfig.tgChat();
        Bandwidth limit = Bandwidth.builder()
            .capacity(config.limit())
            .refillGreedy(config.limit(), config.interval())
            .build();
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    @Bean
    public Bucket LinksRateLimitBucket() {
        ControllerConfig.Links config = controllerConfig.links();
        Bandwidth limit = Bandwidth.builder()
            .capacity(config.limit())
            .refillGreedy(config.limit(), config.interval())
            .build();
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

}
