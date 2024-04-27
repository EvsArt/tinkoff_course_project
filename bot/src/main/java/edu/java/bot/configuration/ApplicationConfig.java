package edu.java.bot.configuration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app")
public record ApplicationConfig(
    @NotEmpty String telegramToken,
    @NotNull Controller controller,
    @NotNull ApplicationConfig.KafkaTopic kafkaUpdatesTopic,
    @NotNull ApplicationConfig.KafkaTopic kafkaUpdatesDLQTopic,
    @DefaultValue ApplicationConfig.Async async
) {

    public record KafkaTopic(String name, int partitions, int replicas) {
    }

    public record Async(@DefaultValue("1") int corePoolSize, @DefaultValue("2048") int maxPoolSize,
                        @DefaultValue("2048") int queueCapacity, @DefaultValue("AsyncThread-") String threadPrefix) {
    }

    public record Controller(Updates updates) {
        public record Updates(
            long limit,
            Duration interval
        ) {
        }
    }

}
