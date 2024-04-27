package edu.java.bot.api.kafka;

import edu.java.bot.service.UpdatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@TestPropertySource(properties = "app.kafka-updates-topic.name=testTopic")
@TestPropertySource(locations = "classpath:/testKafka.env")
public abstract class KafkaIntegrationTest {

    @Autowired
    KafkaUpdatesListener updatesListener;
    @Autowired KafkaErrorHandler kafkaErrorHandler;

    @MockBean UpdatesService updatesService;
    @MockBean KafkaDlqSender dlqSender;

    @Container
    public static KafkaContainer KAFKA;

    static {
        KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.2"))
            .withEmbeddedZookeeper();
        KAFKA.start();
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrapServers", KAFKA::getBootstrapServers);
    }

}
