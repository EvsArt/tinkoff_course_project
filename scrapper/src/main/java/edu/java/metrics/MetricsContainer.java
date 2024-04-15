package edu.java.metrics;

import edu.java.constants.MetricsConstants;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class MetricsContainer {

    private final MeterRegistry meterRegistry;
    private Counter sentKafkaUpdate;
    private Counter sentHTTPUpdate;

    @Autowired
    public MetricsContainer(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        registerCounters();
    }

    private void registerCounters() {
        sentKafkaUpdate = Counter.builder(MetricsConstants.UPDATE_NAME)
            .tag(MetricsConstants.UPDATE_STATUS, MetricsConstants.UPDATE_STATUS_SENT)
            .tag(MetricsConstants.UPDATE_BY, MetricsConstants.UPDATE_TO_KAFKA)
            .description("Sent updates by kafka")
            .register(meterRegistry);

        sentHTTPUpdate = Counter.builder(MetricsConstants.UPDATE_NAME)
            .tag(MetricsConstants.UPDATE_STATUS, MetricsConstants.UPDATE_STATUS_SENT)
            .tag(MetricsConstants.UPDATE_BY, MetricsConstants.UPDATE_TO_HTTP)
            .description("Sent updates by http")
            .register(meterRegistry);
    }
}
