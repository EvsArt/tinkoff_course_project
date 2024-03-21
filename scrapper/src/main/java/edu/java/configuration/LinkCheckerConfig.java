package edu.java.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;

//@ConfigurationProperties(prefix = "app.link-checker", ignoreUnknownFields = true)
public record LinkCheckerConfig(@NotNull Duration checkInterval) {
}
