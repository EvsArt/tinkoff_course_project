package edu.java.repository;

import edu.java.model.TgChat;
import java.util.List;
import java.util.Optional;

public class JdbcTgChatRepository implements TgChatRepository {

    @Override
    public Optional<TgChat> insertTgChat(TgChat chat) {
        return Optional.empty();
    }

    @Override
    public Optional<TgChat> updateTgChat(Long id, TgChat chat) {
        return Optional.empty();
    }

    @Override
    public Optional<TgChat> removeTgChatById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<TgChat> findTgChatById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<TgChat> findAllTgChats() {
        return null;
    }
}
