package edu.java.bot.kafka;

import edu.java.bot.api.dto.LinkUpdateRequest;
import edu.java.bot.metrics.ReceivedKafkaUpdate;
import edu.java.bot.service.UpdatesService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "true")
public class KafkaUpdatesListener {

    private final UpdatesService updatesService;

    public KafkaUpdatesListener(UpdatesService updatesService) {
        this.updatesService = updatesService;
    }

    @KafkaListener(topics = "${app.kafka-updates-topic.name}")
    @ReceivedKafkaUpdate
    public void listen(@Valid LinkUpdateRequest update) {
        log.debug(String.format("Update %s was accepted", update));
        updatesService.sendUpdatesMessages(update);
    }
}
