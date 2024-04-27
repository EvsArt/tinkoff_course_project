package edu.java.bot.configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitingConfig {

    private final ApplicationConfig.Controller controllerConfig;

    public RateLimitingConfig(ApplicationConfig applicationConfig) {
        this.controllerConfig = applicationConfig.controller();
    }

    @Bean
    public Bucket updatesRateLimitBucket() {
        ApplicationConfig.Controller.Updates config = controllerConfig.updates();
        Bandwidth limit = Bandwidth.builder()
            .capacity(config.limit())
            .refillGreedy(config.limit(), config.interval())
            .build();
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

}
