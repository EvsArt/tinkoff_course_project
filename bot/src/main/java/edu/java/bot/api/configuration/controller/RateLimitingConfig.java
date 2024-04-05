package edu.java.bot.api.configuration.controller;

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
    public Bucket updatesRateLimitBucket() {
        ControllerConfig.Updates config = controllerConfig.updates();
        Bandwidth limit = Bandwidth.builder()
            .capacity(config.limit())
            .refillGreedy(config.limit(), config.interval())
            .build();
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

}
