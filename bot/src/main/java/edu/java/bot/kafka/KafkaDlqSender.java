package edu.java.bot.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "true")
public class KafkaDlqSender {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.kafka-updates-dlq-topic.name}")
    private String dlqTopic;

    public KafkaDlqSender(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String msg) {
        kafkaTemplate.send(dlqTopic, msg);
    }

}
