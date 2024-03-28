package edu.java.configuration;

import edu.java.configuration.model.Retry;
import edu.java.constants.DefaultUrl;
import java.net.URL;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "tgbot", ignoreUnknownFields = true)
public record BotConfig(
    @DefaultValue(DefaultUrl.BOT_DEFAULT_URL) URL url,
    Duration connectionTimeout,
    Retry retry
) {
}
