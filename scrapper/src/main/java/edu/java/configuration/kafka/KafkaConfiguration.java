package edu.java.configuration.kafka;

import edu.java.configuration.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

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
    public NewTopic updatesTopic() {
        return TopicBuilder.name(kafkaUpdatesTopic.name())
            .partitions(kafkaUpdatesTopic.partitions())
            .replicas(kafkaUpdatesTopic.replicas())
            .build();
    }

}
