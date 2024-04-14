package edu.java.bot.metrics;

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

    private final Counter sentUpdates = Counter.builder("updates")
        .tag("status", "confirmed")
        .description("Processed and sent to telegram updates")
        .register(meterRegistry);

    private final Counter receivedKafkaUpdate = Counter.builder("updates")
        .tag("status", "received")
        .tag("from", "kafka")
        .description("Received updates")
        .register(meterRegistry);

    private final Counter receivedHTTPUpdate = Counter.builder("updates")
        .tag("status", "received")
        .tag("from", "http")
        .description("Received updates")
        .register(meterRegistry);

    private final Counter errorUpdates = Counter.builder("updates")
        .tag("status", "error")
        .description("Updates with error that sent to dlq")
        .register(meterRegistry);

}
