package edu.java.configuration.controller;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.controller")
public record ControllerConfig(
    @NotNull TgChat tgChat,
    @NotNull Links links
) {

    public record TgChat(
        long limit,
        Duration interval
    ) {
    }

    public record Links(
        long limit,
        Duration interval
    ) {
    }
}
