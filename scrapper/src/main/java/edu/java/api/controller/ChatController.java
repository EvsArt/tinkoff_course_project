package edu.java.api.controller;

import edu.java.exceptions.status.TooManyRequestsException;
import edu.java.model.entity.TgChat;
import edu.java.service.TgChatService;
import io.github.bucket4j.Bucket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/tg-chat")
public class ChatController implements IChatController {

    private final TgChatService chatService;
    private final Bucket bucket;

    public ChatController(TgChatService chatService,
        @Qualifier("TgChatRateLimitBucket") Bucket bucket
    ) {
        this.chatService = chatService;
        this.bucket = bucket;
    }

    @PostMapping("/{id}")
    public void registerChat(@PathVariable Long id) {
        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException();
        }
        TgChat chat = chatService.registerChat(id, "");
        log.debug(String.format("Chat %s was registered", chat));
    }

    @DeleteMapping("/{id}")
    public void deleteChat(@PathVariable Long id) {
        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException();
        }
        TgChat chat = chatService.unregisterChat(id);
        log.debug(String.format("Chat %s was deleted", chat));
    }

}
