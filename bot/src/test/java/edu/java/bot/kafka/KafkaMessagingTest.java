package edu.java.bot.kafka;

import edu.java.bot.api.dto.LinkUpdateRequest;
import edu.java.bot.configuration.ApplicationConfig;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import static org.awaitility.Awaitility.await;

public class KafkaMessagingTest extends KafkaIntegrationTest {

    ApplicationConfig.KafkaTopic topic = new ApplicationConfig.KafkaTopic("testTopic", 1, 1);

    LinkUpdateRequest testLinkUpdateRequest = LinkUpdateRequest.builder()
        .url("aa")
        .description("vvv")
        .tgChatIds(List.of(1L, 2L, 3L))
        .build();

    @Test
    public void testConsumeMessages() {
        try (
            KafkaProducer<String, LinkUpdateRequest> kafkaProducer = new KafkaProducer<>(producerFactory().getConfigurationProperties())) {

            ProducerRecord<String, LinkUpdateRequest> record =
                new ProducerRecord<>(topic.name(), testLinkUpdateRequest);
            kafkaProducer.send(record);

            await()
                .atMost(Duration.ofMillis(5000L))
                .untilAsserted(() -> Mockito.verify(updatesService).sendUpdatesMessages(Mockito.any()));
        }
    }

    @Test
    public void testDLQSendingMessages() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(fakeProducerFactory());

        ProducerRecord<String, String> record =
            new ProducerRecord<>(topic.name(), "WrongMessage");
        kafkaTemplate.send(record);

        await()
            .atMost(Duration.ofMillis(5000L))
            .untilAsserted(() -> Mockito.verify(dlqSender).send(Mockito.any()));
    }

    public ProducerFactory<String, LinkUpdateRequest> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroLinkUpdateRequestSerializerForTests.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    public ProducerFactory<String, String> fakeProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

}
