package edu.java.bot.scrapperClient.config;

import edu.java.bot.constants.DefaultUrl;
import java.net.URL;
import java.time.Duration;
import edu.java.bot.scrapperClient.config.retry.RetryConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "scrapper", ignoreUnknownFields = true)
public record ScrapperConfig(
    @DefaultValue(DefaultUrl.SCRAPPER_DEFAULT_URL) URL url,
    Duration connectionTimeout,
    RetryConfig retry
) {
}
