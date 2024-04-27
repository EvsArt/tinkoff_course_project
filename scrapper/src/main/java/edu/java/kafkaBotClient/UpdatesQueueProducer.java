package edu.java.kafkaBotClient;

import edu.java.botClient.BotClient;
import edu.java.dto.bot.LinkUpdateRequest;
import edu.java.dto.bot.PostUpdatesResponse;
import edu.java.configuration.ApplicationConfig;
import edu.java.metrics.SentKafkaUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class UpdatesQueueProducer implements BotClient {

    private final ApplicationConfig.KafkaUpdatesTopic kafkaUpdatesTopic;
    private final KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    @SentKafkaUpdate
    public Mono<PostUpdatesResponse> postUpdates(LinkUpdateRequest updateRequest) {
        log.debug("Sending update {}", updateRequest);
        kafkaTemplate.send(kafkaUpdatesTopic.name(), updateRequest);
        return Mono.just(new PostUpdatesResponse());
    }

}
