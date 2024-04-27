package edu.java.kafka;

import edu.java.dto.bot.LinkUpdateRequest;
import edu.java.configuration.ApplicationConfig;
import edu.java.service.kafka.LinkUpdateRequestSerializer;
import edu.java.botClient.UpdatesQueueProducer;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KafkaMessagingTest extends KafkaIntegrationTest {

    ApplicationConfig.KafkaUpdatesTopic topic = new ApplicationConfig.KafkaUpdatesTopic("testTopic", 1, 1);

    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate = new KafkaTemplate<>(producerFactory());
    private final UpdatesQueueProducer updatesProducer = new UpdatesQueueProducer(topic, kafkaTemplate);

    @Test
    public void testSendMessage() {
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(List.of(1L, 2L), "aa", "vvv");
        KafkaConsumer<String, LinkUpdateRequest> kafkaConsumer = new KafkaConsumer<>(consumerConfigs());

        updatesProducer.postUpdates(linkUpdateRequest);

        kafkaConsumer.subscribe(List.of(topic.name()));

        ConsumerRecords<String, LinkUpdateRequest> record = kafkaConsumer.poll(Duration.ofMillis(5000L));
        kafkaConsumer.close();

        assertThat(record).isNotNull();
        assertThat(record.count()).isOne();
        assertThat(record.records(topic.name()).iterator().next().value())
            .isEqualTo(linkUpdateRequest);
    }

    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "testGroup");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroDeserializerForTests.class);
        return props;
    }

    public ProducerFactory<String, LinkUpdateRequest> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, LinkUpdateRequestSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

}
