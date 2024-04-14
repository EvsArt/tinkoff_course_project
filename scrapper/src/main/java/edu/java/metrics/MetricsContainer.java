package edu.java.metrics;

import edu.java.constants.MetricsConstants;
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

    private final Counter sentKafkaUpdate = Counter.builder(MetricsConstants.UPDATE_NAME)
        .tag(MetricsConstants.UPDATE_STATUS, MetricsConstants.UPDATE_STATUS_SENT)
        .tag(MetricsConstants.UPDATE_BY, MetricsConstants.UPDATE_TO_KAFKA)
        .description("Sent updates by kafka")
        .register(meterRegistry);

    private final Counter sentHTTPUpdate = Counter.builder(MetricsConstants.UPDATE_NAME)
        .tag(MetricsConstants.UPDATE_STATUS, MetricsConstants.UPDATE_STATUS_SENT)
        .tag(MetricsConstants.UPDATE_BY, MetricsConstants.UPDATE_TO_HTTP)
        .description("Sent updates by http")
        .register(meterRegistry);

}
