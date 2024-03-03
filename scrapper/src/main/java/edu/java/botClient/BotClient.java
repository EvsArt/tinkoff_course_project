package edu.java.botClient;

import edu.java.botClient.dto.LinkUpdateRequest;
import org.springframework.http.ResponseEntity;

public interface BotClient {

    ResponseEntity<Void> postUpdates(LinkUpdateRequest updateRequest);

}
