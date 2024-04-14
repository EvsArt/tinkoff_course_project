package edu.java.bot.metrics;

import edu.java.bot.constants.MetricsConstants;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class MetricsContainer {

    @Getter(AccessLevel.PRIVATE)
    private final MeterRegistry meterRegistry = new SimpleMeterRegistry();

    private final Counter sentUpdates = Counter.builder(MetricsConstants.UPDATE_NAME)
        .tag(MetricsConstants.UPDATE_STATUS, MetricsConstants.UPDATE_STATUS_RECEIVED)
        .description("Processed and sent updates to telegram")
        .register(meterRegistry);

    private final Counter receivedKafkaUpdate = Counter.builder(MetricsConstants.UPDATE_NAME)
        .tag(MetricsConstants.UPDATE_STATUS, MetricsConstants.UPDATE_STATUS_RECEIVED)
        .tag(MetricsConstants.UPDATE_FROM, MetricsConstants.UPDATE_FROM_KAFKA)
        .description("Received update by kafka")
        .register(meterRegistry);

    private final Counter receivedHTTPUpdate = Counter.builder(MetricsConstants.UPDATE_NAME)
        .tag(MetricsConstants.UPDATE_STATUS, MetricsConstants.UPDATE_STATUS_RECEIVED)
        .tag(MetricsConstants.UPDATE_FROM, MetricsConstants.UPDATE_FROM_HTTP)
        .description("Received update by http")
        .register(meterRegistry);

    private final Counter errorUpdates = Counter.builder(MetricsConstants.UPDATE_NAME)
        .tag(MetricsConstants.UPDATE_STATUS, MetricsConstants.UPDATE_STATUS_ERROR)
        .description("Updates with error that sent to dlq")
        .register(meterRegistry);

    private final Counter receivedTgMessages = Counter.builder(MetricsConstants.MESSAGES_NAME)
        .tag(MetricsConstants.MESSAGES_NAME_FROM, MetricsConstants.MESSAGES_NAME_FROM_TG)
        .description("Updates with error that sent to dlq")
        .register(meterRegistry);

}
