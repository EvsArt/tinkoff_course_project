package edu.java.domain.jooqRepository;

import edu.java.model.Link;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.domain.jooq.Tables.LINK_TG_CHAT;

@Slf4j
@Repository
public class JooqAssociativeTableRepository {

    private final DefaultDSLContext dsl;

    @Autowired
    public JooqAssociativeTableRepository(DefaultDSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional
    public void saveLinkAndChatIds(Long id, Link link) {
        log.debug("saveLinkAndChatIds() was called with id={} and link={}", id, link);

        link.getTgChats().forEach(chat -> saveLinkAndChatIds(id, chat.getId()));

        log.debug(
            "saveLinkAndChatsToAssociativeTable(): {} rows were updated in associative table",
            link.getTgChats().size()
        );
    }

    public void saveLinkAndChatIds(Long linkId, Long chatId) {
        log.debug("saveLinkAndChatIds with linkId={} and chatId={}", linkId, chatId);
        dsl.insertInto(LINK_TG_CHAT)
            .set(LINK_TG_CHAT.LINK_ID, linkId)
            .set(LINK_TG_CHAT.TG_CHAT_ID, chatId)
            .execute();
    }

    public List<Long> findLinksIdsByTgChatId(Long id) {
        log.debug("findLinksIdsByTgChatId() with id={}", id);
        return dsl
            .select(LINK_TG_CHAT.LINK_ID)
            .from(LINK_TG_CHAT)
            .where(LINK_TG_CHAT.TG_CHAT_ID.eq(id))
            .fetchInto(Long.class);
    }

    @Transactional
    public List<Long> removeLinkAndChatIdsByTgChatId(Long id) {
        log.debug("removeLinkAndChatIdsByTgChatId() with id={}", id);
        List<Long> removedIds = findLinksIdsByTgChatId(id);
        int updated = dsl.deleteFrom(LINK_TG_CHAT)
            .where(LINK_TG_CHAT.TG_CHAT_ID.eq(id))
            .execute();
        log.debug("removeLinkAndChatIdsByTgChatId(): {} rows were updated", updated);
        return removedIds;
    }

    @Transactional
    public void removeLinkAndChatIds(Long linkId, Long chatId) {
        log.debug("removeLinkAndChatIds with linkId={} and chatId={}", linkId, chatId);
        int updated = dsl.deleteFrom(LINK_TG_CHAT)
            .where(LINK_TG_CHAT.LINK_ID.eq(linkId))
            .and(LINK_TG_CHAT.TG_CHAT_ID.eq(chatId))
            .execute();
        log.debug("removeLinkAndChatIds(): {} rows were updated", updated);
    }

}
