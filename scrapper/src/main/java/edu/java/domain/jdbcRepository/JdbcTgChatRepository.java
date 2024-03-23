package edu.java.domain.jdbcRepository;

import edu.java.domain.TgChatRepository;
import edu.java.model.entity.TgChat;
import edu.java.service.SqlQueries;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class JdbcTgChatRepository implements TgChatRepository {

    private final JdbcClient jdbcClient;
    private final JdbcAssociativeTableRepository associativeTableRepository;

    private final String tgChatTableName = SqlQueries.TG_CHAT_TABLE_NAME;
    private final String associativeTableName = SqlQueries.LINK_TG_CHAT_TABLE_NAME;
    private final List<String> tgChatFieldsNamesWithoutId = SqlQueries.TG_CHAT_FIELDS_NAMES_WITHOUT_ID;

    public JdbcTgChatRepository(
        JdbcClient jdbcClient,
        JdbcAssociativeTableRepository associativeTableRepository
    ) {
        this.jdbcClient = jdbcClient;
        this.associativeTableRepository = associativeTableRepository;
    }

    @Override
    @Transactional
    public Optional<TgChat> insertTgChat(TgChat chat) {
        log.debug("insertTgChat() was called with chat={}", chat);
        String sql = SqlQueries.insertQuery(tgChatTableName, tgChatFieldsNamesWithoutId);
        KeyHolder idHolder = new GeneratedKeyHolder();
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.TG_CHAT_FIELD_CHAT_ID_NAME, chat.getChatId(), Types.BIGINT)
            .param(SqlQueries.TG_CHAT_FIELD_NAME_NAME, chat.getName(), Types.VARCHAR)
            .update(idHolder, SqlQueries.TG_CHAT_FIELD_ID_NAME);
        if (updated == 0) {
            return Optional.empty();
        }
        long id = idHolder.getKey().longValue();
        log.info("insertTgChat(): chat saved with id={}", id);
        return findTgChatById(id);
    }

    @Override
    @Transactional
    public Optional<TgChat> updateTgChat(Long id, TgChat chat) {
        log.debug("updateTgChat() was called with id={}: chat={}", id, chat);
        String sql =
            SqlQueries.updateQuery(tgChatTableName, tgChatFieldsNamesWithoutId, SqlQueries.TG_CHAT_FIELD_ID_NAME);
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.TG_CHAT_FIELD_CHAT_ID_NAME, chat.getChatId(), Types.BIGINT)
            .param(SqlQueries.TG_CHAT_FIELD_NAME_NAME, chat.getName(), Types.VARCHAR)
            .param(SqlQueries.TG_CHAT_FIELD_ID_NAME, id, Types.BIGINT)  // id in searching condition
            .update();
        log.info("updateTgChat(): {} rows were updated", updated);
        return findTgChatById(id);
    }

    @Override
    @Transactional
    public Optional<TgChat> removeTgChatById(Long id) {
        log.debug("removeTgChatById() was called with id={}", id);
        String sql = SqlQueries.deleteQuery(tgChatTableName, SqlQueries.TG_CHAT_FIELD_ID_NAME);
        Optional<TgChat> oldTgChat = findTgChatById(id);
        if (oldTgChat.isEmpty()) {
            return oldTgChat;
        }
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.TG_CHAT_FIELD_ID_NAME, id, Types.BIGINT)
            .update();
        log.debug("removeTgChatById(): {} rows were updated", updated);
        associativeTableRepository.removeLinkAndChatIdsByTgChatId(id);
        return oldTgChat;
    }

    @Override
    public Optional<TgChat> removeTgChatByChatId(Long chatId) {
        log.debug("removeTgChatByChatId() was called with chatId={}", chatId);
        String sql = SqlQueries.deleteQuery(tgChatTableName, SqlQueries.TG_CHAT_FIELD_CHAT_ID_NAME);
        Optional<TgChat> oldTgChat = findTgChatByChatId(chatId);
        if (oldTgChat.isEmpty()) {
            return oldTgChat;
        }
        int updated = jdbcClient.sql(sql)
            .param(SqlQueries.TG_CHAT_FIELD_CHAT_ID_NAME, chatId, Types.BIGINT)
            .update();
        log.debug("removeTgChatByChatId(): {} rows were updated", updated);
        associativeTableRepository.removeLinkAndChatIdsByTgChatId(oldTgChat.get().getId());
        return oldTgChat;
    }

    @Override
    @Transactional
    public Optional<TgChat> findTgChatById(Long id) {
        log.debug("findTgChatById() was called with id={}", id);
        String sql = SqlQueries.findWhereQuery(tgChatTableName, SqlQueries.TG_CHAT_FIELD_ID_NAME);
        return jdbcClient.sql(sql)
            .param(SqlQueries.TG_CHAT_FIELD_ID_NAME, id, Types.BIGINT)
            .query(TgChat.class)
            .optional();
    }

    @Override
    @Transactional
    public Optional<TgChat> findTgChatByChatId(Long chatId) {
        log.debug("findTgChatByChatId() was called with chatId={}", chatId);
        String sql = SqlQueries.findWhereQuery(tgChatTableName, SqlQueries.TG_CHAT_FIELD_CHAT_ID_NAME);
        return jdbcClient.sql(sql)
            .param(SqlQueries.TG_CHAT_FIELD_CHAT_ID_NAME, chatId, Types.BIGINT)
            .query(TgChat.class)
            .optional();
    }

    @Override
    @Transactional
    public List<TgChat> findAllTgChats() {
        log.debug("findAllTgChats() was called");
        String sql = SqlQueries.findAllQuery(tgChatTableName);
        return jdbcClient.sql(sql)
            .query(TgChat.class).list();
    }

    @Override
    @Transactional
    public List<TgChat> findTgChatsByLinkId(Long id) {
        log.debug("findTgChatsByLinkId() was called with id={}", id);
        String sql = SqlQueries.findFieldWhereQuery(
            associativeTableName,
            SqlQueries.LINK_TG_CHAT_FIELD_TG_CHAT_NAME,
            SqlQueries.LINK_TG_CHAT_FIELD_LINK_NAME
        );
        return new ArrayList<>(jdbcClient.sql(sql)
            .param(SqlQueries.LINK_TG_CHAT_FIELD_LINK_NAME, id, Types.BIGINT)
            .query(Long.class).list()
            .stream()
            .map(tgChatId -> findTgChatById(tgChatId).get())
            .toList());
    }

}
