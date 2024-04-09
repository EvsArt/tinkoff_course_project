package edu.java.bot.kafka;

import edu.java.bot.api.dto.LinkUpdateRequest;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.UpdatesService;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.context.TestPropertySource;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@TestPropertySource(properties = "app.kafka-updates-topic.name=testTopic")
public class KafkaMessagingTest extends KafkaIntegrationTest {

    @Autowired KafkaUpdatesListener updatesListener;
    @MockBean UpdatesService updatesService;

    @Autowired KafkaErrorHandler kafkaErrorHandler;
    @MockBean KafkaDlqSender dlqSender;

    ApplicationConfig.KafkaTopic topic = new ApplicationConfig.KafkaTopic("testTopic", 1, 1);

    LinkUpdateRequest testLinkUpdateRequest = LinkUpdateRequest.builder()
        .url("aa")
        .description("vvv")
        .tgChatIds(List.of(1L, 2L, 3L))
        .build();

    @Test
    public void testConsumeMessages() throws ExecutionException, InterruptedException {
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
    public void testDLQSendingMessages() throws ExecutionException, InterruptedException {
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
