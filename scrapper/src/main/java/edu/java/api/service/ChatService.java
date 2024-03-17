package edu.java.api.service;

import edu.java.api.repository.InMemoryChatRepository;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final InMemoryChatRepository chatRepository;

    public ChatService(InMemoryChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public boolean registry(Long id) {
        return chatRepository.registry(id);
    }

    public boolean delete(Long id) {
        return chatRepository.delete(id);
    }

}
