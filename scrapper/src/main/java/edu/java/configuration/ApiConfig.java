package edu.java.configuration;

import edu.java.constants.StringService;
import jakarta.validation.constraints.NotNull;
import java.net.URL;
import java.time.Duration;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.util.MultiValueMap;

@ConfigurationProperties(prefix = "api")
public record ApiConfig(
    @NotNull GitHubConfig gitHub,
    @NotNull StackOverflowConfig stackOverflow
) {
    public record GitHubConfig(
        @DefaultValue(StringService.GITHUB_DEFAULT_URL) URL url,
        MultiValueMap<String, String> uriParameters,
        Duration connectionTimeout
    ) {
    }

    public record StackOverflowConfig(
        @DefaultValue(StringService.STACKOVERFLOW_DEFAULT_URL) URL url,
        MultiValueMap<String, String> uriParameters,
        Duration connectionTimeout
    ) {
    }

}
