package edu.java.configuration.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.botClient.dto.LinkUpdateRequest;
import edu.java.configuration.ApplicationConfig;
import edu.java.kafkaBotClient.ScrapperQueueProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "true")
@EnableKafka
@Slf4j
public class KafkaConfiguration {

    private final ApplicationConfig.KafkaUpdatesTopic kafkaUpdatesTopic;

    public KafkaConfiguration(ApplicationConfig applicationConfig) {
        this.kafkaUpdatesTopic = applicationConfig.kafkaUpdatesTopic();
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(kafkaUpdatesTopic.name())
            .partitions(kafkaUpdatesTopic.partitions())
            .replicas(kafkaUpdatesTopic.replicas())
            .build();
    }

    @Bean
    public ScrapperQueueProducer scrapperQueueProducer(
        ApplicationConfig applicationConfig,
        KafkaTemplate<Long, LinkUpdateRequest> kafkaTemplate
    ) {
        return new ScrapperQueueProducer(applicationConfig.kafkaUpdatesTopic(), kafkaTemplate);
    }

}
