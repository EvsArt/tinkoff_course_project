package edu.java.kafkaBotClient;

import edu.java.botClient.BotClient;
import edu.java.botClient.dto.LinkUpdateRequest;
import edu.java.botClient.dto.PostUpdatesResponse;
import edu.java.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class UpdatesQueueProducer implements BotClient {

    private final ApplicationConfig.KafkaUpdatesTopic kafkaUpdatesTopic;
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    public Mono<PostUpdatesResponse> postUpdates(LinkUpdateRequest updateRequest) {
        log.debug("Sending update {}", updateRequest);
        kafkaTemplate.send(kafkaUpdatesTopic.name(), updateRequest);
        return Mono.just(new PostUpdatesResponse());
    }

}
