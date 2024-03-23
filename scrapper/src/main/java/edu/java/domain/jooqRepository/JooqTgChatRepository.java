package edu.java.domain.jooqRepository;

import edu.java.domain.TgChatRepository;
import edu.java.model.entity.TgChat;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.domain.jooq.Tables.LINK_TG_CHAT;
import static edu.java.domain.jooq.Tables.TG_CHAT;

@Slf4j
@Repository
public class JooqTgChatRepository implements TgChatRepository {

    private final DefaultDSLContext dsl;
    private final JooqAssociativeTableRepository associativeTableRepository;

    public JooqTgChatRepository(
        DefaultDSLContext dsl,
        JooqAssociativeTableRepository associativeTableRepository
    ) {
        this.dsl = dsl;
        this.associativeTableRepository = associativeTableRepository;
    }

    @Override
    public Optional<TgChat> insertTgChat(TgChat chat) {
        log.debug("insertTgChat() was called with chat={}", chat);
        int updated = dsl.insertInto(TG_CHAT)
            .set(TG_CHAT.CHAT_ID, chat.getChatId())
            .set(TG_CHAT.NAME, chat.getName())
            .execute();
        log.debug("Executed: {}", updated);
        if (updated == 0) {
            return Optional.empty();
        }
        Optional<TgChat> savedChat = findTgChatByChatId(chat.getChatId());
        savedChat.ifPresent((ch) -> log.info("insertTgChat(): chat saved chat={}", ch));
        return savedChat;
    }

    @Override
    @Transactional
    public Optional<TgChat> updateTgChat(Long id, TgChat chat) {
        log.debug("updateTgChat() was called with id={}: chat={}", id, chat);
        int updated = dsl
            .update(TG_CHAT)
            .set(TG_CHAT.CHAT_ID, chat.getChatId())
            .set(TG_CHAT.NAME, chat.getName())
            .where(TG_CHAT.ID.eq(id))
            .execute();

        log.info("updateTgChat(): {} rows were updated", updated);
        return findTgChatById(id);
    }

    @Override
    @Transactional
    public Optional<TgChat> removeTgChatById(Long id) {
        log.debug("removeTgChatById() was called with id={}", id);
        Optional<TgChat> oldTgChat = findTgChatById(id);
        int updated = dsl.deleteFrom(TG_CHAT)
            .where(TG_CHAT.ID.eq(id))
            .execute();
        if (oldTgChat.isEmpty()) {
            return oldTgChat;
        }
        log.debug("removeTgChatById(): {} rows were updated", updated);
        associativeTableRepository.removeLinkAndChatIdsByTgChatId(id);
        return oldTgChat;
    }

    @Override
    public Optional<TgChat> removeTgChatByChatId(Long chatId) {
        log.debug("removeTgChatByChatId() was called with chatId={}", chatId);
        Optional<TgChat> oldTgChat = findTgChatByChatId(chatId);
        int updated = dsl.deleteFrom(TG_CHAT)
            .where(TG_CHAT.CHAT_ID.eq(chatId))
            .execute();
        if (oldTgChat.isEmpty()) {
            return oldTgChat;
        }
        log.debug("removeTgChatByChatId(): {} rows were updated", updated);
        associativeTableRepository.removeLinkAndChatIdsByTgChatId(oldTgChat.get().getId());
        return oldTgChat;
    }

    @Override
    @Transactional
    public Optional<TgChat> findTgChatById(Long id) {
        log.debug("findTgChatById() was called with id={}", id);
        return dsl.select()
            .from(TG_CHAT)
            .where(TG_CHAT.ID.eq(id))
            .fetchOptionalInto(TgChat.class);
    }

    @Override
    public Optional<TgChat> findTgChatByChatId(Long chatId) {
        log.debug("findTgChatByChatId() was called with chatId={}", chatId);
        return dsl.select()
            .from(TG_CHAT)
            .where(TG_CHAT.CHAT_ID.eq(chatId))
            .fetchOptionalInto(TgChat.class);
    }

    @Override
    public List<TgChat> findAllTgChats() {
        log.debug("findAllTgChats() was called");
        return dsl.select()
            .from(TG_CHAT)
            .fetchInto(TgChat.class);
    }

    @Override
    public List<TgChat> findTgChatsByLinkId(Long linkId) {
        log.debug("findTgChatsByLinkId() was called with linkId={}", linkId);
        return dsl.select(TG_CHAT)
            .from(LINK_TG_CHAT)
            .join(TG_CHAT)
            .on(TG_CHAT.ID.eq(LINK_TG_CHAT.TG_CHAT_ID))

            .where(LINK_TG_CHAT.LINK_ID.eq(linkId))
            .fetchInto(TgChat.class);
    }

}
