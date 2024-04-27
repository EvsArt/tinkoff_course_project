package edu.java.botClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.java.dto.bot.LinkUpdateRequest;
import edu.java.dto.bot.PostUpdatesResponse;
import reactor.core.publisher.Mono;

public interface BotClient {

    Mono<PostUpdatesResponse> postUpdates(LinkUpdateRequest updateRequest) throws JsonProcessingException;

}
