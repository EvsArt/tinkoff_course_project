package edu.java.bot.configuration;

import edu.java.bot.constants.DefaultUrl;
import edu.java.bot.scrapperClient.retry.RetryConfig;
import java.net.URL;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "scrapper", ignoreUnknownFields = true)
public record ScrapperConfig(
    @DefaultValue(DefaultUrl.SCRAPPER_DEFAULT_URL) URL url,
    Duration connectionTimeout,
    RetryConfig retry
) {
}
