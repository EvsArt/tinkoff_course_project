package edu.java.bot.metrics;

import edu.java.bot.constants.MetricsConstants;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class MetricsContainer {

    private final MeterRegistry meterRegistry;

    private Counter sentUpdates;
    private Counter receivedKafkaUpdate;
    private Counter receivedHTTPUpdate;
    private Counter errorUpdates;
    private Counter receivedTgMessages;

    @Autowired
    public MetricsContainer(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        registerCounters();
    }

    private void registerCounters() {
        sentUpdates = Counter.builder(MetricsConstants.UPDATE_NAME)
            .tag(MetricsConstants.UPDATE_STATUS, MetricsConstants.UPDATE_STATUS_RECEIVED)
            .tag(MetricsConstants.UPDATE_FROM, MetricsConstants.UPDATE_FROM)
            .description("Processed and sent updates to telegram")
            .register(meterRegistry);

        receivedKafkaUpdate = Counter.builder(MetricsConstants.UPDATE_NAME)
            .tag(MetricsConstants.UPDATE_STATUS, MetricsConstants.UPDATE_STATUS_RECEIVED)
            .tag(MetricsConstants.UPDATE_FROM, MetricsConstants.UPDATE_FROM_KAFKA)
            .description("Received update by kafka")
            .register(meterRegistry);

        receivedHTTPUpdate = Counter.builder(MetricsConstants.UPDATE_NAME)
            .tag(MetricsConstants.UPDATE_STATUS, MetricsConstants.UPDATE_STATUS_RECEIVED)
            .tag(MetricsConstants.UPDATE_FROM, MetricsConstants.UPDATE_FROM_HTTP)
            .description("Received update by http")
            .register(meterRegistry);

        errorUpdates = Counter.builder(MetricsConstants.UPDATE_NAME)
            .tag(MetricsConstants.UPDATE_STATUS, MetricsConstants.UPDATE_STATUS_ERROR)
            .tag(MetricsConstants.UPDATE_FROM, MetricsConstants.UPDATE_FROM_KAFKA)
            .description("Updates with error that sent to dlq")
            .register(meterRegistry);

        receivedTgMessages = Counter.builder(MetricsConstants.MESSAGES_NAME)
            .tag(MetricsConstants.MESSAGES_NAME_FROM, MetricsConstants.MESSAGES_NAME_FROM_TG)
            .description("Messages that received by Telegram")
            .register(meterRegistry);
    }

}
