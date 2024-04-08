package edu.java.bot.configuration.kafka;

import edu.java.bot.api.dto.LinkUpdateRequest;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.bot.service.UpdatesService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.retrytopic.DeadLetterPublishingRecovererFactory;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.support.RetryTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "app", name = "useQueue", havingValue = "true")
@EnableKafka
public class KafkaConfiguration {

    private final ApplicationConfig.KafkaUpdatesTopic kafkaUpdatesTopic;

    public KafkaConfiguration(
            ApplicationConfig applicationConfig
    ) {
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
    public DeadLetterPublishingRecoverer recoverer(KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate) {
        return new DeadLetterPublishingRecoverer(kafkaTemplate);
    }

    @Bean
    public KafkaListenerErrorHandler eh(DeadLetterPublishingRecoverer recoverer) {
        return (msg, ex) -> {
            if (msg.getHeaders().get(KafkaHeaders.DELIVERY_ATTEMPT, Integer.class) > 0) {
                log.error("I'M HERE!!!");
                recoverer.accept(msg.getHeaders().get(KafkaHeaders.RAW_DATA, ConsumerRecord.class), ex);
                return "FAILED";
            }
            throw ex;
        };
    }

    @Bean
    public ErrorHandlingDeserializer<LinkUpdateRequest> h() {
        var eh = new ErrorHandlingDeserializer<LinkUpdateRequest>();
        eh.setFailedDeserializationFunction(
            (info) -> {
                log.error(info.toString());
                return LinkUpdateRequest.builder().build();
            }
        );
        return eh;
    }

    @Bean
    public ProducerFactory<String, LinkUpdateRequest> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, LinkUpdateRequest> retryableTopicKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, LinkUpdateRequest> listenerFactory(ConsumerFactory<String, LinkUpdateRequest> cf) {
//
//        ConcurrentKafkaListenerContainerFactory<String, LinkUpdateRequest> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(cf);
//
//        RetryTemplate rt = RetryTemplate.defaultInstance();
//        factory.setReplyTemplate();setRetryTemplate(rt);
//        factory.setErrorHandler(((exception, data) -> {
//            /*
//             * here you can do you custom handling, I am just logging it same as default
//             * Error handler does If you just want to log. you need not configure the error
//             * handler here. The default handler does it for you. Generally, you will
//             * persist the failed records to DB for tracking the failed records.
//             */
//            log.error("Error in process with Exception {} and the record is {}", exception, data);
//        }));
//
//        return factory;

}
