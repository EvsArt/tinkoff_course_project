package edu.java.kafkaBotClient;

import edu.java.botClient.BotClient;
import edu.java.botClient.dto.LinkUpdateRequest;
import edu.java.botClient.dto.PostUpdatesResponse;
import edu.java.configuration.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ScrapperQueueProducer implements BotClient {

    private final ApplicationConfig.KafkaUpdatesTopic kafkaUpdatesTopic;
    private final KafkaTemplate<Long, LinkUpdateRequest> kafkaTemplate;

    public ScrapperQueueProducer(
        ApplicationConfig applicationConfig,
        KafkaTemplate<Long, LinkUpdateRequest> kafkaTemplate
    ) {
        this.kafkaUpdatesTopic = applicationConfig.kafkaUpdatesTopic();
        this.kafkaTemplate = kafkaTemplate;
    }

    public Mono<PostUpdatesResponse> postUpdates(LinkUpdateRequest updateRequest) {
        kafkaTemplate.send(kafkaUpdatesTopic.name(), updateRequest);
        return Mono.just(new PostUpdatesResponse());
    }

}
