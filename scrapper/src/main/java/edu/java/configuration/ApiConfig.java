package edu.java.configuration;

import edu.java.configuration.retry.RetryConfig;
import edu.java.constants.DefaultUrl;
import jakarta.validation.constraints.NotNull;
import java.net.URL;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.MultiValueMap;

@ConfigurationProperties(prefix = "api")
public record ApiConfig(
    @NotNull GitHubConfig gitHub,
    @NotNull StackOverflowConfig stackOverflow
) {
    public record GitHubConfig(
        @DefaultValue(DefaultUrl.GITHUB_DEFAULT_URL) URL url,
        MultiValueMap<String, String> uriParameters,
        Duration connectionTimeout,
        RetryConfig retry
    ) {
    }

    public record StackOverflowConfig(
        @DefaultValue(DefaultUrl.STACKOVERFLOW_DEFAULT_URL) URL url,
        MultiValueMap<String, String> uriParameters,
        Duration connectionTimeout,
        RetryConfig retry
    ) {
    }

}
