package edu.java.kafkaBotClient;

import edu.java.botClient.dto.LinkUpdateRequest;
import edu.java.botClient.dto.PostUpdatesResponse;
import edu.java.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdatesQueueProducer {

    private final ApplicationConfig.KafkaUpdatesTopic kafkaUpdatesTopic;
    private final KafkaTemplate<Long, LinkUpdateRequest> kafkaTemplate;

    public Mono<PostUpdatesResponse> postUpdates(LinkUpdateRequest updateRequest) {
        kafkaTemplate.send(kafkaUpdatesTopic.name(), updateRequest);
        return Mono.just(new PostUpdatesResponse());
    }

}
