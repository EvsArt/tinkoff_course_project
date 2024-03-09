package edu.java.api.controller;

import edu.java.api.exceptions.ChatAlreadyRegisteredException;
import edu.java.api.exceptions.ChatNotExistException;
import edu.java.api.service.ChatService;
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

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/{id}")
    public void registerChat(@PathVariable Long id) {

        if (!chatService.registry(id)) {
            throw new ChatAlreadyRegisteredException();
        }

        log.debug(String.format("Chat %d was registered", id));
    }

    @DeleteMapping("/{id}")
    public void deleteChat(@PathVariable Long id) {

        if (!chatService.delete(id)) {
            throw new ChatNotExistException();
        }

        log.debug(String.format("Chat %d was deleted", id));
    }

}
