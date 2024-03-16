package edu.java.repository;

import edu.java.model.TgChat;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface TgChatRepository {

    Optional<TgChat> insertTgChat(TgChat chat);

    Optional<TgChat> updateTgChat(Long id, TgChat chat);

    Optional<TgChat> removeTgChatById(Long id);

    Optional<TgChat> findTgChatById(Long id);

    List<TgChat> findAllTgChats();

}
