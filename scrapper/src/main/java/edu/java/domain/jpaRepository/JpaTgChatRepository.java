package edu.java.domain.jpaRepository;

import edu.java.model.entity.TgChat;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaTgChatRepository extends JpaRepository<TgChat, Long> {
    Optional<TgChat> findTgChatByChatId(long tgChatId);
    int deleteByChatId(long tgChatId);
}
