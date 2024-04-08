package edu.java.bot.configuration.kafka;

import edu.java.bot.api.dto.LinkUpdateRequest;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.UpdatesService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.listener.ConsumerProperties;

@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "true")
@EnableKafka
public class KafkaConfiguration {

    private final ApplicationConfig.KafkaUpdatesTopic kafkaUpdatesTopic;
    private final UpdatesService updatesService;

    public KafkaConfiguration(ApplicationConfig applicationConfig, UpdatesService updatesService) {
        this.kafkaUpdatesTopic = applicationConfig.kafkaUpdatesTopic();
        this.updatesService = updatesService;
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(kafkaUpdatesTopic.name())
            .partitions(kafkaUpdatesTopic.partitions())
            .replicas(kafkaUpdatesTopic.replicas())
            .build();
    }

    @KafkaListener(topics = "${app.kafka-updates-topic.name}")
    public void listen(@Valid LinkUpdateRequest update) {
        log.debug(String.format("Update %s was accepted", update));
        updatesService.sendUpdatesMessages(update);
    }

}
