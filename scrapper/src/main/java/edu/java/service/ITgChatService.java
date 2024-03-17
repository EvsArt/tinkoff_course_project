package edu.java.service;

import edu.java.model.TgChat;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ITgChatService implements TgChatService {

    private final TgChatRepository tgChatRepository;
    private final LinkRepository linkRepository;

    @Autowired
    public ITgChatService(TgChatRepository tgChatRepository, LinkRepository linkRepository) {
        this.tgChatRepository = tgChatRepository;
        this.linkRepository = linkRepository;
    }

    /**
     * Register new chat by its chat id and name and returns it
     * if this chat already registered returns it
     * if chat not registered, but it didn't write returns its clone without id
     *
     * @param chatId id of chat from TG
     * @param name   name of this chat
     * @return saved tgChat
     * @throws RuntimeException if chat not registered, but it hasn't written
     */
    @Override
    public TgChat registerChat(long chatId, String name) {
        Optional<TgChat> existsChat = tgChatRepository.findTgChatByChatId(chatId);
        return existsChat
            .orElseGet(
                () -> tgChatRepository.insertTgChat(new TgChat(chatId, name))
                    .orElse(new TgChat(chatId, name))
                );
    }

    /**
     * Delete chat by its chat id returns it
     * or its clone without id if it has not registered
     *
     * @param chatId id of chat from TG
     * @return deleted tgChat
     */
    @Override
    public TgChat unregisterChat(long chatId) {
        Optional<TgChat> existsChat = tgChatRepository.removeTgChatById(chatId);
        linkRepository.removeLinksByTgChatId(chatId);
        return existsChat.orElse(new TgChat(chatId, ""));
    }

}
