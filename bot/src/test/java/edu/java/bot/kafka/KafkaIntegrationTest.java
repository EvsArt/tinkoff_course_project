package edu.java.bot.kafka;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@DirtiesContext
public class KafkaIntegrationTest {

    public static KafkaContainer KAFKA;

    static {
        KAFKA = new KafkaContainer(new DockerImageName("confluentinc/cp-kafka:7.3.2"))
            .withEmbeddedZookeeper()
            .withExposedPorts(9093);
        KAFKA.start();
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrapServers", KAFKA::getBootstrapServers);
    }

}
