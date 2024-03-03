package edu.java.api.controller;

import edu.java.api.InMemoryChatRepository;
import edu.java.api.exceptions.ChatAlreadyRegisteredException;
import edu.java.api.exceptions.ChatNotExistException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/tg-chat")
public class ChatController implements IChatController {

    private final InMemoryChatRepository chatRepository;

    public ChatController(InMemoryChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> registerChat(@PathVariable Long id) {

        if (!chatRepository.registry(id)) {
            throw new ChatAlreadyRegisteredException();
        }

        log.debug(String.format("Chat %d was registered", id));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {

        if (!chatRepository.delete(id)) {
            throw new ChatNotExistException();
        }

        log.debug(String.format("Chat %d was deleted", id));
        return ResponseEntity.ok().build();
    }

}
