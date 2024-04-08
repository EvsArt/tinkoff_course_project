package edu.java.bot.configuration.kafka;

import edu.java.bot.api.dto.LinkUpdateRequest;
import edu.java.bot.service.UpdatesService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@Configuration
public class KafkaUpdatesListener {

    private final UpdatesService updatesService;

    public KafkaUpdatesListener(UpdatesService updatesService) {
        this.updatesService = updatesService;
    }

    @KafkaListener(topics = "${app.kafka-updates-topic.name}")
    public void listen(@Valid LinkUpdateRequest update) {
        log.debug(String.format("Update %s was accepted", update));
        updatesService.sendUpdatesMessages(update);
    }

    @DltHandler
    public void handleDltPayment(String update, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Event on dlt topic={}, payload={}", topic, update);
    }

}
