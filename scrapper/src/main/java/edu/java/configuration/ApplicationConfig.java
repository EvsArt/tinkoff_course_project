package edu.java.configuration;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = true)
public record ApplicationConfig(
    @NotNull Scheduler scheduler,
    @NotNull LinkChecker linkChecker,
    @NotNull AccessType databaseAccessType
) {

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record LinkChecker(@NotNull Duration checkInterval) {
    }

    public enum AccessType {
        JDBC, JPA, JOOQ
    }

}
