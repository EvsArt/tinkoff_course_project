package edu.java.configuration;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = true)
public record ApplicationConfig(
    @NotNull Scheduler scheduler,
    @NotNull LinkChecker linkChecker,
    @NotNull AccessType databaseAccessType
) {

    public enum AccessType {
        JDBC, JPA, JOOQ
    }

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record LinkChecker(@NotNull Duration checkInterval) {
    }

}
