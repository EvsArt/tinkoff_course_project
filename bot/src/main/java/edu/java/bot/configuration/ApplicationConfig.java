package edu.java.bot.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record ApplicationConfig(
    @NotEmpty String telegramToken,
    @NotNull KafkaUpdatesTopic kafkaUpdatesTopic
) {

    public record KafkaUpdatesTopic(String name, int partitions, int replicas) {
    }

}
