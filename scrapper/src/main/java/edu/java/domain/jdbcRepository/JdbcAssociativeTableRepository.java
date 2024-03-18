package edu.java.domain.jdbcRepository;

import edu.java.model.Link;
import edu.java.service.SqlQueries;
import java.sql.Types;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Slf4j
public class JdbcAssociativeTableRepository {

    private final JdbcClient jdbcClient;

    private final String associativeTableName = SqlQueries.LINK_TG_CHAT_TABLE_NAME;

    public JdbcAssociativeTableRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
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
        String sql = SqlQueries.insertQuery(
            associativeTableName,
            List.of(SqlQueries.LINK_TG_CHAT_FIELD_LINK_NAME, SqlQueries.LINK_TG_CHAT_FIELD_TG_CHAT_NAME)
        );
        jdbcClient.sql(sql)
            .param(SqlQueries.LINK_TG_CHAT_FIELD_LINK_NAME, linkId, Types.BIGINT)
            .param(SqlQueries.LINK_TG_CHAT_FIELD_TG_CHAT_NAME, chatId, Types.BIGINT)
            .update();
    }

    public List<Long> findLinksIdsByTgChatId(Long id) {
        log.debug("findLinksIdsByTgChatId() with id={}", id);
        String sql = SqlQueries.findFieldWhereQuery(
            associativeTableName,
            SqlQueries.LINK_TG_CHAT_FIELD_LINK_NAME,
            SqlQueries.LINK_TG_CHAT_FIELD_TG_CHAT_NAME
        );
        return jdbcClient.sql(sql)
            .param(SqlQueries.LINK_TG_CHAT_FIELD_TG_CHAT_NAME, id, Types.BIGINT)
            .query(Long.class).list();
    }

    @Transactional
    public List<Long> removeLinkAndChatIdsByTgChatId(Long id) {
        log.debug("removeLinkAndChatIdsByTgChatId() with id={}", id);
        List<Long> removedIds = findLinksIdsByTgChatId(id);
        String sql = SqlQueries.deleteQuery(
            associativeTableName,
            SqlQueries.LINK_TG_CHAT_FIELD_TG_CHAT_NAME
        );
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.LINK_TG_CHAT_FIELD_TG_CHAT_NAME, id, Types.BIGINT)
            .update();
        log.debug("removeLinkAndChatIdsByTgChatId(): {} rows were updated", updated);
        return removedIds;
    }

    @Transactional
    public void removeLinkAndChatIds(Long linkId, Long chatId) {
        log.debug("removeLinkAndChatIds with linkId={} and chatId={}", linkId, chatId);
        String sql = SqlQueries.deleteQuery(
            associativeTableName,
            List.of(SqlQueries.LINK_TG_CHAT_FIELD_LINK_NAME, SqlQueries.LINK_TG_CHAT_FIELD_TG_CHAT_NAME)
        );
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.LINK_TG_CHAT_FIELD_LINK_NAME, linkId, Types.BIGINT)
            .param(SqlQueries.LINK_TG_CHAT_FIELD_TG_CHAT_NAME, chatId, Types.BIGINT)
            .update();
        log.debug("removeLinkAndChatIds(): {} rows were updated", updated);
    }

}
