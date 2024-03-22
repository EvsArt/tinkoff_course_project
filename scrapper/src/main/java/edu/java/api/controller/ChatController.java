package edu.java.api.controller;

import edu.java.model.entity.TgChat;
import edu.java.service.TgChatService;
import lombok.extern.slf4j.Slf4j;
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

    public ChatController(TgChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/{id}")
    public void registerChat(@PathVariable Long id) {
        TgChat chat = chatService.registerChat(id, "");
        log.debug(String.format("Chat %s was registered", chat));
    }

    @DeleteMapping("/{id}")
    public void deleteChat(@PathVariable Long id) {
        TgChat chat = chatService.unregisterChat(id);
        log.debug(String.format("Chat %s was deleted", chat));
    }

}
