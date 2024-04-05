package edu.java.bot.api.configuration.controller;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.controller")
public record ControllerConfig(
    @NotNull Updates updates
) {

    public record Updates(
        long limit,
        Duration interval
    ) {
    }
}
