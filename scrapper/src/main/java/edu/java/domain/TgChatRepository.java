package edu.java.domain;

import edu.java.model.entity.TgChat;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface TgChatRepository {

    Optional<TgChat> insertTgChat(TgChat chat);

    Optional<TgChat> updateTgChat(Long id, TgChat chat);

    Optional<TgChat> removeTgChatById(Long id);

    Optional<TgChat> removeTgChatByChatId(Long chatId);

    Optional<TgChat> findTgChatById(Long id);

    Optional<TgChat> findTgChatByChatId(Long chatId);

    List<TgChat> findAllTgChats();

    List<TgChat> findTgChatsByLinkId(Long id);
}
