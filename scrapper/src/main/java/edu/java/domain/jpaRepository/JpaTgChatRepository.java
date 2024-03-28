package edu.java.domain.jpaRepository;

import edu.java.model.entity.TgChat;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public interface JpaTgChatRepository extends JpaRepository<TgChat, Long> {
    Optional<TgChat> findTgChatByChatId(long tgChatId);

    int deleteByChatId(long tgChatId);
}
