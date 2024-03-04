package edu.java.botClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.java.botClient.dto.LinkUpdateRequest;
import edu.java.botClient.dto.PostUpdatesResponse;
import reactor.core.publisher.Mono;

public interface BotClient {

    Mono<PostUpdatesResponse> postUpdates(LinkUpdateRequest updateRequest) throws JsonProcessingException;

}
