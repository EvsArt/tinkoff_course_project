package edu.java.bot.configuration;

import edu.java.bot.configuration.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "true")
@EnableKafka
public class KafkaConfiguration {

    private final ApplicationConfig.KafkaTopic kafkaTopic;

    public KafkaConfiguration(
        ApplicationConfig applicationConfig
    ) {
        this.kafkaTopic = applicationConfig.kafkaUpdatesTopic();
    }

    @Bean
    public NewTopic updatesTopic() {
        return TopicBuilder.name(kafkaTopic.name())
            .partitions(kafkaTopic.partitions())
            .replicas(kafkaTopic.replicas())
            .build();
    }

}
