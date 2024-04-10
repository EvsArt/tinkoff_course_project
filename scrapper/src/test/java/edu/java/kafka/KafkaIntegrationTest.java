package edu.java.kafka;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@TestPropertySource(properties = "app.kafka-updates-topic.name=testTopic")
public class KafkaIntegrationTest {

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
