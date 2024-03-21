package edu.java.service.jdbc;

import edu.java.domain.jdbcRepository.JdbcLinkRepository;
import edu.java.domain.jdbcRepository.JdbcTgChatRepository;
import edu.java.exceptions.ChatAlreadyRegisteredException;
import edu.java.exceptions.ChatNotExistException;
import edu.java.model.TgChat;
import edu.java.service.TgChatService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JdbcTgChatService implements TgChatService {

    private final JdbcTgChatRepository tgChatRepository;
    private final JdbcLinkRepository linkRepository;

    @Autowired
    public JdbcTgChatService(JdbcTgChatRepository tgChatRepository, JdbcLinkRepository linkRepository) {
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
        boolean chatExisted = tgChatRepository.findTgChatByChatId(chatId).isPresent();
        log.info("Register chat chatId={}, name={}", chatId, name);
        if (chatExisted) {
            throw new ChatAlreadyRegisteredException();
        }
        return tgChatRepository.insertTgChat(new TgChat(chatId, name)).get();
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
        log.info("Unregister chat chatId={}", chatId);
        Optional<TgChat> existsChat = tgChatRepository.findTgChatByChatId(chatId);
        if (existsChat.isEmpty()) {
            throw new ChatNotExistException();
        }
        linkRepository.removeLinksByTgChatId(existsChat.get().getId());
        return tgChatRepository.removeTgChatByChatId(chatId).get();
    }

}
