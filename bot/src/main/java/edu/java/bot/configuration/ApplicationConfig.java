package edu.java.bot.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record ApplicationConfig(
    @NotEmpty String telegramToken,
    @NotNull ApplicationConfig.KafkaTopic kafkaUpdatesTopic,
    @NotNull ApplicationConfig.KafkaTopic kafkaUpdatesDLQTopic
) {

    public record KafkaTopic(String name, int partitions, int replicas) {
    }

}
